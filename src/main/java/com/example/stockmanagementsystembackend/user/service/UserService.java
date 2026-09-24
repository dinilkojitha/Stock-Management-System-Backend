package com.example.stockmanagementsystembackend.user.service;

import com.example.stockmanagementsystembackend.domain.organization.entity.Department;
import com.example.stockmanagementsystembackend.domain.organization.repository.DepartmentRepository;
import com.example.stockmanagementsystembackend.user.dto.request.UserRequest;
import com.example.stockmanagementsystembackend.user.dto.response.UserResponse;
import com.example.stockmanagementsystembackend.user.entity.Role;
import com.example.stockmanagementsystembackend.user.entity.User;
import com.example.stockmanagementsystembackend.user.repository.UserRepository;
import com.example.stockmanagementsystembackend.user.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       DepartmentRepository departmentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public UserResponse getById(Integer id) {
        return toResponse(find(id));
    }

    @Transactional
    public UserResponse update(Integer id, UserRequest request) {
        User user = find(id);
        validatePassword(request, false);
        apply(user, request, false);
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Integer id) {
        userRepository.delete(find(id));
    }

    private void apply(User user, UserRequest request, boolean creating) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        user.setFullName(trim(request.getFullName()));
        user.setEmail(trim(request.getEmail()));
        user.setPhoneNumber(trim(request.getPhoneNumber()));
        user.setRole(findRole(request.getRoleId()));
        user.setDepartment(findDepartment(request.getDepartmentId()));
        if (creating || request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }

    private User find(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }











}
