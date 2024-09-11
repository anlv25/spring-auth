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
    private final RuleRedisRepository ruleRedisRepository;
    private final RuleService ruleService;
    private final RuleRepository ruleRepository;

    @PostMapping
    public String updateRuleToRedis() {
        ruleRedisRepository.deleteAll();
        ruleRedisRepository.saveAll(ruleService.convertEntityToRedis(ruleRepository.findAll()));
        return "Security configuration has been reset successfully.";
    }
}
