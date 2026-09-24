package com.example.stockmanagementsystembackend.user.service;

import com.example.stockmanagementsystembackend.user.repository.RoleRepository;
import com.example.stockmanagementsystembackend.user.entity.Role;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Role createRole(Role role) {
        validateRole(role);
        role.setId(null);
        return roleRepository.save(role);
    }

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role getRoleById(Integer id) {
        return roleRepository.findById(id).orElseThrow(() -> roleNotFound(id));
    }

    @Transactional
    public Role updateRole(Integer id, Role role) {
        validateRole(role);
        Role existingRole = getRoleById(id);
        existingRole.setName(role.getName());
        existingRole.setAccessLevel(role.getAccessLevel());
        return roleRepository.save(existingRole);
    }

    Transactional
    public void deleteRole(Integer id) {
        Role role = getRoleById(id);
        try {
            roleRepository.delete(role);
            roleRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role cannot be deleted because it is assigned to a user");
        }
    }












}