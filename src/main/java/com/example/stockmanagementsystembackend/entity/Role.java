package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleID", nullable = false)
    private Integer id;

    @Size(max = 45)
    @Column(name = "roleName", length = 45)
    private String roleName;

    @Column(name = "access_type")
    private Integer accessType;

}