package com.anlv.security.common.exception;

public class OTPInvalidException extends RuntimeException {
    public OTPInvalidException(String message) {
        super(message);
    }
    public OTPInvalidException() {
        super("OTP Không hợp lệ!");
    }
}