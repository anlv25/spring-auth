package com.anlv.security.exception;

public class ForeignKeyViolationException extends RuntimeException {
    public ForeignKeyViolationException(String message) {
        super(message);
    }
}