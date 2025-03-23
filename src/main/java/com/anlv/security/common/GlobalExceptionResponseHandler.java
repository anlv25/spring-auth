package com.anlv.security.common;


import com.anlv.security.common.exception.EmailAlreadyExistsException;
import com.anlv.security.common.exception.EmailNotExistsException;
import com.anlv.security.common.exception.ForeignKeyViolationException;
import com.anlv.security.common.exception.OTPInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;


import java.time.Instant;


@RestControllerAdvice
public class GlobalExceptionResponseHandler {

    @ExceptionHandler(value = {EmailAlreadyExistsException.class, OTPInvalidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDTO mapInvalidCallException(Exception ex,WebRequest request) {

        return ErrorMessageDTO.builder()
                .message(ex.getMessage())
                .date(Instant.now())
                .build();
    }

    @ExceptionHandler(value = {ResponseStatusException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessageDTO unAuth(Exception ex,WebRequest request) {

        return ErrorMessageDTO.builder()
                .message(ex.getMessage())
                .date(Instant.now())
                .build();
    }

    @ExceptionHandler(value = {EmailNotExistsException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageDTO mapNotFoundException(Exception ex, WebRequest request) {

        return ErrorMessageDTO.builder()
                .message(ex.getMessage())
                .date(Instant.now())
                .build();
    }
    @ExceptionHandler(value = {ForeignKeyViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessageDTO config(Exception ex, WebRequest request) {

        return ErrorMessageDTO.builder()
                .message(ex.getMessage())
                .date(Instant.now())
                .build();
    }
}
