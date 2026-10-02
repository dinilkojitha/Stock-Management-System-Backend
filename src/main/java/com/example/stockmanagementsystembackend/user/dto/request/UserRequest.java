package com.example.stockmanagementsystembackend.user.dto.request;

import com.example.stockmanagementsystembackend.domain.organization.entity.Department;
import com.example.stockmanagementsystembackend.user.entity.Role;
import jakarta.validation.constraints.NotNull;


public class UserRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private Integer roleId;
    private Integer departmentId;
    private String password;

    // Getters
    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}