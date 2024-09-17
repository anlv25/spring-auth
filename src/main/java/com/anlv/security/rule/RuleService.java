package com.anlv.security.rule;

import com.anlv.security.role.Role;
import com.anlv.security.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleService {
    private final RuleRepository ruleRepository;
    private final RuleRedisRepository ruleRedisRepository;
    private final RoleRepository roleRepository;

    public List<RuleRedis> getAllRules() {

        return (List<RuleRedis>) ruleRedisRepository.findAll();
    }

    public Rule createRule(RuleRequest ruleRequest) {
        Rule rule = new Rule();
        updateRuleFromRequest(rule, ruleRequest);
        Rule savedRule = ruleRepository.save(rule);
        updateRuleToRedis();
        return savedRule;
    }

    public Rule updateRule(Long id, RuleRequest ruleRequest) {
        return ruleRepository.findById(id)
            .map(rule -> {
                updateRuleFromRequest(rule, ruleRequest);
                Rule saved = ruleRepository.save(rule);
                updateRuleToRedis();
                return saved;
            })
            .orElseThrow(() -> new RuntimeException("Rule not found with id: " + id));
    }

    private void updateRuleFromRequest(Rule rule, RuleRequest ruleRequest) {
        rule.setExp_path(ruleRequest.getExp_path());
        rule.setExp_path_fe(ruleRequest.getExp_path_fe());
        Role role = roleRepository.findById(ruleRequest.getRoleId())
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + ruleRequest.getRoleId()));
        rule.setRole(role);
        rule.setCreateRequest(ruleRequest.isCreateRequest());
        rule.setUpdateRequest(ruleRequest.isUpdateRequest());
        rule.setReadRequest(ruleRequest.isReadRequest());
        rule.setDeleteRequest(ruleRequest.isDeleteRequest());
    }

    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
        updateRuleToRedis();
    }

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
