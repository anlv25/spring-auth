package com.anlv.security.permission;

import com.anlv.security.role.Role;
import com.anlv.security.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionRedisRepository permissionRedisRepository;
    private final RoleRepository roleRepository;

    public List<PermissionRedis> getAllRules() {

        return (List<PermissionRedis>) permissionRedisRepository.findAll();
    }

    public Permission createRule(PermissionRequest permissionRequest) {
        Permission permission = new Permission();
        updateRuleFromRequest(permission, permissionRequest);
        Permission savedPermission = permissionRepository.save(permission);
        updateRuleToRedis();
        return savedPermission;
    }

    public Permission updateRule(Long id, PermissionRequest permissionRequest) {
        return permissionRepository.findById(id)
            .map(permission -> {
                updateRuleFromRequest(permission, permissionRequest);
                Permission saved = permissionRepository.save(permission);
                updateRuleToRedis();
                return saved;
            })
            .orElseThrow(() -> new RuntimeException("Rule not found with id: " + id));
    }

    private void updateRuleFromRequest(Permission permission, PermissionRequest permissionRequest) {
        permission.setExp_path(permissionRequest.getExp_path());
        permission.setExp_path_fe(permissionRequest.getExp_path_fe());
        Role role = roleRepository.findById(permissionRequest.getRoleId())
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + permissionRequest.getRoleId()));
        permission.setRole(role);
        permission.setCreateRequest(permissionRequest.isCreateRequest());
        permission.setUpdateRequest(permissionRequest.isUpdateRequest());
        permission.setReadRequest(permissionRequest.isReadRequest());
        permission.setDeleteRequest(permissionRequest.isDeleteRequest());
    }

    public void deleteRule(Long id) {
        permissionRepository.deleteById(id);
        updateRuleToRedis();
    }

    private   List<PermissionRedis> convertEntityToRedis(List<Permission> permissions) {
        List<PermissionRedis> permissionRedises = new ArrayList<>();
        for (Permission permission : permissions) {
            PermissionRedis permissionRedis = new PermissionRedis();
            permissionRedis.setId(permission.getId());
            permissionRedis.setExp_path(permission.getExp_path());
            permissionRedis.setExp_path_fe(permission.getExp_path_fe());
            permissionRedis.setRole(permission.getRole());
            permissionRedis.setCreateRequest(permission.isCreateRequest());
            permissionRedis.setUpdateRequest(permission.isUpdateRequest());
            permissionRedis.setReadRequest(permission.isReadRequest());
            permissionRedis.setDeleteRequest(permission.isDeleteRequest());
            permissionRedises.add(permissionRedis);
        }
        return permissionRedises;
    }

    public void updateRuleToRedis() {
        permissionRedisRepository.deleteAll();
        permissionRedisRepository.saveAll(convertEntityToRedis(permissionRepository.findAll()));
    }
}
