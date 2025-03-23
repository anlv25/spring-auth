package com.anlv.security.auth;

import com.anlv.security.common.ResponseOK;
import com.anlv.security.otp.OtpRequest;
import com.anlv.security.user.UserRespone;
import com.anlv.security.util.ResponseEntityExp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.anlv.security.common.exception.EmailAlreadyExistsException;

@RestController
@RequestMapping("${pre-api}/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

  private final AuthenticationService service;
  private final LogoutService logoutService;

  @PostMapping("/register")
  public ResponseEntity<?> register(
      @RequestBody RegisterRequest request
  ) {
        UserRespone response = service.register(request);
        return ResponseEntity.ok(response);

  }
  @PostMapping("/authenticate")
  public ResponseEntity<?> authenticate(
      @RequestBody AuthenticationRequest request,
      HttpServletRequest httpServletRequest
  ) {
        return ResponseEntity.ok(service.authenticate(request,httpServletRequest.getRemoteAddr()));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request, HttpServletRequest httpServletRequest) {
    return ResponseEntity.ok(service.refreshToken(request.getRefreshToken(), httpServletRequest.getRemoteAddr()));
  }

  @PostMapping("/verify-otp")
  public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest) {
    return service.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp());
  }
  @PostMapping("/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    logoutService.logout(request, response, authentication);
  }
  @PostMapping("/re-password")
  public ResponseEntity<?> verifyOtpRePass(@RequestBody  RePasswordRequest rePasswordRequest) {
    String rs = service.rePassword(rePasswordRequest.getEmail());
    return ResponseEntityExp.get(HttpStatus.OK, new ResponseOK(rs));
  }

  @PostMapping("/verify-otp-repass")
  public ResponseEntity<?> verifyOtp(@RequestBody RePasswordVerifyRequest rePasswordVerifyRequest) {
    String rs = service.verifyOtpRePass(rePasswordVerifyRequest);
    return ResponseEntityExp.get(HttpStatus.OK,new ResponseOK(rs));
  }
}
