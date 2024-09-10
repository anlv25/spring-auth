package com.anlv.security.rule;

import com.anlv.security.config.SecurityConfigurationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rules")
@RequiredArgsConstructor
@Slf4j
public class RuleController {
    private final SecurityConfigurationManager securityConfigurationManager;
    public String updateRule() {
        try {
            securityConfigurationManager.updateSecurityConfiguration();
        } catch (Exception e) {
            log.error("Update rule failed");
            return "failed";
        }
        return "success";
    }
}
