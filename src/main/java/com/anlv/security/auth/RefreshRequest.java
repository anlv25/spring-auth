package com.anlv.security.auth;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
