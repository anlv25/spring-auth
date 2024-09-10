package com.anlv.security.rule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleService {
    private final RuleRepository ruleRepository;
    public List<Rule> getRules() {
        return ruleRepository.findAll();
    }
}
