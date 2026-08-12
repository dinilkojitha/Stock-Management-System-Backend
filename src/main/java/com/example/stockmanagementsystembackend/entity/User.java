package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userID", nullable = false)
    private Integer id;

    @Size(max = 150)
    @Column(name = "fullName", length = 150)
    private String fullName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_roleID", nullable = false)
    private Role roleRoleid;

    @Size(max = 60)
    @Column(name = "email", length = 60)
    private String email;

    @Lob
    @Column(name = "password")
    private String password;

    @Size(max = 45)
    @Column(name = "phoneNumber", length = 45)
    private String phoneNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Department_departmentId", nullable = false)
    private Department departmentDepartment;

}