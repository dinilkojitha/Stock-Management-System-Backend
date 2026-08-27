package com.example.stockmanagementsystembackend.service;

import com.example.stockmanagementsystembackend.entity.Department;
import com.example.stockmanagementsystembackend.entity.Role;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class InventoryItemService {

    @Id
    private Integer id;

    private String fullName;

    @NotNull
    private Role roleRoleid;

    private String email;

    private String phoneNumber;

    @NotNull
    private Department departmentDepartment;

}
