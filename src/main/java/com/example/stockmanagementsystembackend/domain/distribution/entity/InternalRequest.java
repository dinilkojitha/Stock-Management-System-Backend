package com.example.stockmanagementsystembackend.domain.distribution.entity;

import com.example.stockmanagementsystembackend.domain.organization.entity.Department;
import com.example.stockmanagementsystembackend.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "internalrequest")
public class InternalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordertID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Department_departmentId", nullable = false)
    private Department departmentDepartment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "User_userID", nullable = false)
    private User userUserid;

    @Column(name = "requestTime")
    private Instant requestTime;

    @Size(max = 45)
    @Column(name = "status", length = 45)
    private String status;

}
