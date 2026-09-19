package com.example.stockmanagementsystembackend.user.entity;

import com.example.stockmanagementsystembackend.domain.organization.entity.Department;
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
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Size(max = 150)
    @Column(name = "full_name", length = 150)
    private String fullName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Size(max = 60)
    @Column(name = "email", length = 60)
    private String email;

    @Lob
    @Column(name = "password")
    private String password;

    @Size(max = 45)
    @Column(name = "phone_number", length = 45)
    private String phoneNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

}