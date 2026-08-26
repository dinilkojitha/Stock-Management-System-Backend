package com.example.stockmanagementsystembackend.dto.request;

import com.example.stockmanagementsystembackend.entity.Department;
import com.example.stockmanagementsystembackend.entity.Role;
import jakarta.validation.constraints.NotNull;

public class UserRequest {

    private String fullName;

    @NotNull
    private Role roleRoleid;

    private String email;

    private String password;

    private String phoneNumber;

    @NotNull
    private Department departmentDepartment;
}
