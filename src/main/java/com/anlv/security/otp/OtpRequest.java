package com.anlv.security.otp;

import lombok.Data;

@Data
public class OtpRequest {
    private String email;
    private String otp;
}