package com.anlv.security.auth;

import com.anlv.security.common.exception.EmailAlreadyExistsException;
import com.anlv.security.common.exception.EmailNotExistsException;
import com.anlv.security.common.ResponseOK;
import com.anlv.security.common.exception.OTPInvalidException;
import com.anlv.security.config.JwtService;
import com.anlv.security.email.EmailService;
import com.anlv.security.otp.OTP;
import com.anlv.security.otp.OTPRepository;
import com.anlv.security.otp.OtpType;
import com.anlv.security.role.Role;
import com.anlv.security.role.RoleRepository;
import com.anlv.security.token.Token;
import com.anlv.security.token.TokenRepository;
import com.anlv.security.token.TokenType;
import com.anlv.security.user.*;
import com.anlv.security.util.ResponseEntityExp;
import com.anlv.security.util.StringUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final PreUserRepository preUserRepository;
  private final EmailService emailService;
  private final OTPRepository otpRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;

  @Value("${application.security.jwt.expiration}")
  private long accessTokenExpiration;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshTokenExpiration;
  @Transactional
  public UserRespone register(RegisterRequest request) {
    // Kiểm tra xem email đã tồn tại chưa
    if (repository.findByEmail(request.getEmail()).isPresent()) {
        throw new EmailAlreadyExistsException();
    }

    Optional<PreUser> preUser = preUserRepository.findByEmail(request.getEmail());
    preUser.ifPresent(preUserRepository::delete);
    preUserRepository.flush();

    var user = PreUser.builder()
            .fullName(request.getFullName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();
    var savedUser = preUserRepository.save(user);

    otpRepository.deleteAllByEmailAndOtpType(request.getEmail(),OtpType.REGISTER);
    otpRepository.flush();

    // Gửi OTP sau khi lưu người dùng
    String otp = StringUtil.generateOtp();
    otpRepository.save(OTP.builder()
            .otp(otp)
            .email(request.getEmail())
            .otpExpiryDate(LocalDateTime.now().plusMinutes(10))
            .otpType(OtpType.REGISTER)
            .build());
    emailService.sendOtp(savedUser.getEmail(), otp);

    return UserRespone.builder().email(savedUser.getEmail()).fullName(savedUser.getFullName()).build();
  }

  public ResponseEntity<?> verifyOtp(String email, String otp) {
    // Kiểm tra OTP với giá trị đã lưu trong cơ sở dữ liệu hoặc bộ nhớ tạm thời
    Optional<OTP> oOtp = otpRepository.findFirstByEmailAndOtpAndOtpType(email,otp,OtpType.REGISTER);
    if(oOtp.isPresent()){
      if(oOtp.get().getOtpExpiryDate().isAfter(LocalDateTime.now())){

         var pre = preUserRepository.findByEmail(email);
         if(pre.isPresent()){
           var preUser = pre.get();
           Optional<Role> role = roleRepository.findById(2L);
           var user = User.builder().fullName(preUser.getFullName()).email(preUser.getEmail()).password(preUser.getPassword()).role(role.get()).build();
           repository.save(user);
         }else{
             return  ResponseEntityExp.get(HttpStatus.UNAUTHORIZED,"Không thấy email đăng ký! Vui lòng đăng ký lại.");
         }

        return ResponseEntity.ok(new ResponseOK("OTP xác thực thành công"));
      }
    }
      return  ResponseEntityExp.get(HttpStatus.UNAUTHORIZED,"OTP không hợp lệ");
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request, String ip) {
    try {

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        var user = repository.findByEmail(request.getEmail())
            .orElseThrow();
        //tokenRepository.deleteAllInValidTokenByUser(user.getId());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        
        // Không thu hồi token cũ

        saveUserToken(user, jwtToken,ip);
        
        UserRespone ur = UserRespone.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .image(user.getImage())
                .role(user.getRole().getName())
                .build();
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .accessTokenExpires((System.currentTimeMillis() + accessTokenExpiration))
                .refreshTokenExpires((System.currentTimeMillis() + refreshTokenExpiration))
                .user(ur)
                .build();
    } catch (BadCredentialsException e) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Đăng nhập không thành công", e);
    }
  }

  private void saveUserToken(User user, String jwtToken, String ip) {
    var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .ipAddress(ip)
            .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
     var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
     if (validUserTokens.isEmpty())
         return;
     validUserTokens.forEach(token -> {
         token.setExpired(true);
         token.setRevoked(true);
     });
     tokenRepository.saveAll(validUserTokens);
  }

  public AuthenticationResponse refreshToken (
          String refreshToken,
          String ip
  ) {
    String userEmail;
    if (refreshToken == null) {
      return null;
    }
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);

        //revokeAllUserTokens(user);
        saveUserToken(user, accessToken,ip);
        UserRespone ur = UserRespone.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .image(user.getImage())
                .role(user.getRole().getName())
                .build();
          return AuthenticationResponse.builder()
                  .accessToken(accessToken)
                  .refreshToken(refreshToken)
                  .accessTokenExpires((System.currentTimeMillis() + accessTokenExpiration))
                  .refreshTokenExpires((System.currentTimeMillis() + refreshTokenExpiration))
                  .user(ur)
                  .build();
      }

    }
    return null;
  }
    @Transactional
    public String rePassword (String email) {
        if (repository.findByEmail(email.trim()).isEmpty()) {
            throw new EmailNotExistsException();
        }
        otpRepository.deleteAllByEmailAndOtpType(email,OtpType.REPASSWORD);
        otpRepository.flush();
        String otp = StringUtil.generateOtp();
        otpRepository.save(OTP.builder()
                .otp(otp)
                .email(email)
                .otpExpiryDate(LocalDateTime.now().plusMinutes(10))
                .otpType(OtpType.REPASSWORD)
                .build());
        emailService.sendOtp(email, otp);

        return "Gửi otp thành công!";
    }

    public String verifyOtpRePass(RePasswordVerifyRequest rePasswordVerifyRequest) {
        Optional<OTP> oOtp = otpRepository.findFirstByEmailAndOtpAndOtpType(rePasswordVerifyRequest.getEmail(),
                rePasswordVerifyRequest.getOtp(),OtpType.REPASSWORD);
        if(oOtp.isPresent()){
            if(oOtp.get().getOtpExpiryDate().isAfter(LocalDateTime.now())){

                var pre = userRepository.findByEmail(rePasswordVerifyRequest.getEmail());
                if(pre.isPresent()){
                    var user = pre.get();
                    user.setPassword(passwordEncoder.encode(rePasswordVerifyRequest.getPassword()));
                    repository.save(user);
                }else{
                    throw new EmailNotExistsException();
                }

                return "Đổi mật khẩu thành công";
            }
        }
        throw new OTPInvalidException();
    }
}
