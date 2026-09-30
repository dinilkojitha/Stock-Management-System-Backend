package com.example.stockmanagementsystembackend.domain.procurement.entity;

import com.example.stockmanagementsystembackend.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "supplier_evaluation")
public class SupplierEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluated_by_user_id", nullable = false)
    private User evaluatedBy;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "delivery_rating")
    private Integer deliveryRating;

    @Column(name = "quality_rating")
    private Integer qualityRating;

    @Column(name = "comments", length = 500)
    private String comments;

    @Column(name = "evaluated_on", nullable = false)
    private LocalDate evaluatedOn;
}