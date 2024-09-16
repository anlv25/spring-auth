package com.anlv.security.auth;

import com.anlv.security.user.UserRespone;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;
import com.anlv.security.auth.exception.EmailAlreadyExistsException;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<?> register(
      @RequestBody RegisterRequest request
  ) {
    try {
        UserRespone response = service.register(request);
        return ResponseEntity.ok(response);
    } catch (EmailAlreadyExistsException e) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).body("Email đã tồn tại!");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi đăng ký");
    }
  }
  @PostMapping("/authenticate")
  public ResponseEntity<?> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    try {
        return ResponseEntity.ok(service.authenticate(request));
    } catch (ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE)).body("Có gì đó sai sai!");
    }
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


}
