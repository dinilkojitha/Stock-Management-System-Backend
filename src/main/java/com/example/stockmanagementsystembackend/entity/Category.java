package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "categoryName must not be blank")
    @Size(max = 45, message = "categoryName must not exceed 45 characters")
    @Column(name = "categoryName", length = 45)
    private String categoryName;

    @Lob
    @Column(name = "discription")
    private String discription;

}
