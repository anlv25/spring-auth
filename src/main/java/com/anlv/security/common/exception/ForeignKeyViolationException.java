package com.anlv.security.common.exception;

public class ForeignKeyViolationException extends RuntimeException {
    public ForeignKeyViolationException(String message) {
        super(message);
    }
    public ForeignKeyViolationException() {
        super("Lỗi thao tác với CSDL");
    }
}