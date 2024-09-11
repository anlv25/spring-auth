package com.anlv.security.rule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleService {
    private final RuleRepository ruleRepository;
    private final RuleRedisRepository ruleRedisRepository;

    private   List<RuleRedis> convertEntityToRedis(List<Rule> rules) {
        List<RuleRedis> ruleRedises = new ArrayList<>();
        for (Rule rule : rules) {
            RuleRedis ruleRedis = new RuleRedis();
            ruleRedis.setId(rule.getId());
            ruleRedis.setExp_path(rule.getExp_path());
            ruleRedis.setExp_path_fe(rule.getExp_path_fe());
            ruleRedis.setRole(rule.getRole());
            ruleRedis.setCreateRequest(rule.isCreateRequest());
            ruleRedis.setUpdateRequest(rule.isUpdateRequest());
            ruleRedis.setReadRequest(rule.isReadRequest());
            ruleRedis.setDeleteRequest(rule.isDeleteRequest());
            ruleRedises.add(ruleRedis);
        }
        return ruleRedises;
    }

    public void updateRuleToRedis() {
        ruleRedisRepository.deleteAll();
        ruleRedisRepository.saveAll(convertEntityToRedis(ruleRepository.findAll()));
    }
}
