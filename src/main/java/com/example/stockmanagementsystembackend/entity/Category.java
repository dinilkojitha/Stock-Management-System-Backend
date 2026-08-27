package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryID", nullable = false)
    private Integer id;

    @Size(max = 45)
    @Column(name = "categoryName", length = 45)
    private String categoryName;

    @Lob
    @Column(name = "description")
    private String description;

}