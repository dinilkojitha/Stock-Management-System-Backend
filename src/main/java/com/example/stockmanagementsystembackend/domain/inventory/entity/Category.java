package com.example.stockmanagementsystembackend.domain.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Category")
public class Category {
    // Encapsulation: persistent state is private and accessed through methods.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @NotBlank(message = "Category name must not be blank")
    @Size(max = 45, message = "Category name must not exceed 45 characters")
    @Column(name = "name", length = 45)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;



}
