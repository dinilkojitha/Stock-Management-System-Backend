package com.example.stockmanagementsystembackend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String fullName;
    private Integer roleId;
    private String roleName;
    private String email;
    private String phoneNumber;
    private Integer departmentId;
    private String departmentName;
}
