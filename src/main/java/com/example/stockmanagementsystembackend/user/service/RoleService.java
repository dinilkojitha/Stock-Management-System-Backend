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





}