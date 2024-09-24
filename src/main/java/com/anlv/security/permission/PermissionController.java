package com.anlv.security.permission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${pre-api}/permissions")
@RequiredArgsConstructor
@Slf4j
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<PermissionRedis>> getAllRules() {
        return ResponseEntity.ok(permissionService.getAllRules());
    }

    @PostMapping
    public ResponseEntity<Permission> createRule(@RequestBody PermissionRequest permissionRequest) {
        return ResponseEntity.ok(permissionService.createRule(permissionRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permission> updateRule(@PathVariable Long id, @RequestBody PermissionRequest permissionRequest) {
        return ResponseEntity.ok(permissionService.updateRule(id, permissionRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        permissionService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update-redis")
    public ResponseEntity<String> updateRuleToRedis() {
        permissionService.updateRuleToRedis();
        return ResponseEntity.ok("Security configuration has been reset successfully.");
    }
}
