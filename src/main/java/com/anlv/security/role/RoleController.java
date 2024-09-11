package com.anlv.security.role;


import com.anlv.security.exception.ForeignKeyViolationException;
import com.anlv.security.role.exeption.DuplicateRoleNameException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody RoleRequest roleRequest) {
        try {
            Role createdRole = roleService.createRole(roleRequest);
            return ResponseEntity.ok(createdRole);
        } catch (DuplicateRoleNameException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody RoleRequest roleRequest) {
        try {
            Role updatedRole = roleService.updateRole(id, roleRequest);
            return ResponseEntity.ok(updatedRole);
        } catch (com.anlv.security.role.exeption.DuplicateRoleNameException | ForeignKeyViolationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.noContent().build();
        } catch (ForeignKeyViolationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}