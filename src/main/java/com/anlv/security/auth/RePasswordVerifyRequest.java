package com.anlv.security.auth;

import lombok.Data;

@Data
public class RePasswordVerifyRequest {
    private String email;
    private String password;
    private String otp;
}
