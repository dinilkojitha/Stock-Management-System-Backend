package com.example.stockmanagementsystembackend.user.service;

import com.example.stockmanagementsystembackend.domain.organization.entity.Department;
import com.example.stockmanagementsystembackend.domain.organization.repository.DepartmentRepository;
import com.example.stockmanagementsystembackend.user.dto.loginReqDto;
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
    public UserResponse create(UserRequest request) {
        validatePassword(request, true);
        User user = new User();
        apply(user, request, true);
        return toResponse(userRepository.save(user));
    }

    public User login(loginReqDto data) {
        User user = userRepository.findByEmail(String.valueOf(data.getEmail()));
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + data.getEmail());
        }else {
            if (!passwordEncoder.matches(data.getPassword(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
            }
            User user1 = new User();
            user1.setId(user.getId());
            user1.setFullName(user.getFullName());
            user1.setEmail(user.getEmail());
            user1.setPhoneNumber(user.getPhoneNumber());
            user1.setRole(user.getRole());
            user1.setDepartment(user.getDepartment());
            return user1;
        }

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
        Role role = findRole(request.getRoleId());
        user.setRole(role);
        Integer departmentId = request.getDepartmentId();
        if (departmentId == null) {
            if (!isAdmin(role)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A department is required for non-admin users"
                );
            }
            user.setDepartment(null);
        } else {
            user.setDepartment(findDepartment(departmentId));
        }
        if (creating || request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }

    private User find(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    private Role findRole(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + id));
    }

    private Department findDepartment(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found: " + id));
    }

    private void validatePassword(UserRequest request, boolean required) {
        if (request == null || required && (request.getPassword() == null || request.getPassword().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password must not be blank");
        }
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getRole().getId(),
                user.getRole().getName(), user.getEmail(), user.getPhoneNumber(),
                user.getDepartment() == null ? null : user.getDepartment().getId(),
                user.getDepartment() == null ? null : user.getDepartment().getName());
    }

    private boolean isAdmin(Role role) {
        return role.getName() != null && "admin".equalsIgnoreCase(role.getName().trim());
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }


//    private String fullName;
//    private String email;
//    private String phoneNumber;
//    private Integer roleId;
//    private Integer departmentId;
//    private String password;

    public  String createAdmin() {
        UserRequest admin = new UserRequest();
        admin.setFullName("Admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword("00000000");
        admin.setRoleId(2);
        User user = new User();
        apply(user, admin, true);
        toResponse(userRepository.save(user));
        return "Success";
    }

}
