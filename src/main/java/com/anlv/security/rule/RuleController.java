package com.anlv.security.rule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rules")
@RequiredArgsConstructor
@Slf4j
public class RuleController {
    private final RuleService ruleService;

    @PostMapping
    public String updateRuleToRedis() {
        ruleService.updateRuleToRedis();
        return "Security configuration has been reset successfully.";
    }
}
