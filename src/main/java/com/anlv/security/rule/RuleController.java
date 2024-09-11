package com.anlv.security.rule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rules")
@RequiredArgsConstructor
@Slf4j
public class RuleController {
    private final RuleService ruleService;

    @GetMapping
    public ResponseEntity<List<Rule>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    @PostMapping
    public ResponseEntity<Rule> createRule(@RequestBody RuleRequest ruleRequest) {
        return ResponseEntity.ok(ruleService.createRule(ruleRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rule> updateRule(@PathVariable Long id, @RequestBody RuleRequest ruleRequest) {
        return ResponseEntity.ok(ruleService.updateRule(id, ruleRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update-redis")
    public ResponseEntity<String> updateRuleToRedis() {
        ruleService.updateRuleToRedis();
        return ResponseEntity.ok("Security configuration has been reset successfully.");
    }
}
