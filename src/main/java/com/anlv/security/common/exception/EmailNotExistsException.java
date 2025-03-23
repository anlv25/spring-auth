package com.anlv.security.common.exception;

public class EmailNotExistsException extends RuntimeException {
    public EmailNotExistsException(String message) {
        super(message);
    }
    public EmailNotExistsException() {
        super("Email không tồn tại!");
    }
}