package com.anlv.security.common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class ErrorMessageDTO implements Serializable {

    private String message;

    private Instant date;

}