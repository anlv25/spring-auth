package com.alibou.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class SecurityConfigurationManager {

    private final HttpSecurity httpSecurity;

    public SecurityConfigurationManager(HttpSecurity httpSecurity) {
        this.httpSecurity = httpSecurity;
    }

    public void updateSecurityConfiguration(SecurityConfigurationDTO config) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth -> {
                    // Apply new configuration
                    for (String path : config.getPublicPaths()) {
                        auth.requestMatchers(path).permitAll();
                    }
                    // Add more configuration options as needed
                })
                .build();
    }
}
