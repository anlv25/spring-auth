package com.anlv.security.role;




import com.anlv.security.common.exception.ForeignKeyViolationException;
import com.anlv.security.role.exeption.DuplicateRoleNameException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role createRole(RoleRequest roleRequest) {
        String upperCaseName = roleRequest.getName().toUpperCase();
        if (roleRepository.existsByNameIgnoreCase(upperCaseName)) {
            throw new DuplicateRoleNameException("Role with name " + upperCaseName + " already exists");
        }
        Role role = new Role();
        role.setName(upperCaseName);
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, RoleRequest roleRequest) {
        return roleRepository.findById(id)
            .map(role -> {
                String upperCaseName = roleRequest.getName().toUpperCase();
                if (!role.getName().equals(upperCaseName) && roleRepository.existsByNameIgnoreCase(upperCaseName)) {
                    throw new DuplicateRoleNameException("Role with name " + upperCaseName + " already exists");
                }
                role.setName(upperCaseName);
                try {
                    return roleRepository.save(role);
                } catch (DataIntegrityViolationException e) {
                    throw new ForeignKeyViolationException("Cannot update role. It is being used by other entities.");
                }
            })
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    public void deleteRole(Long id) {
        try {
            roleRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ForeignKeyViolationException("Cannot delete role. It is being used by other entities.");
        }
    }
}
