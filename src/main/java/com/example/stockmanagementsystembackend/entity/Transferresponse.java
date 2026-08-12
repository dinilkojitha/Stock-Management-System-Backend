package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "transferresponse")
public class Transferresponse {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BranchTransferRequeast_TransferRequeastID", nullable = false)
    private Branchtransferrequeast branchtransferrequeastTransferrequeastid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "User_userID", nullable = false)
    private User userUserid;

    @Column(name = "respondDate")
    private Instant respondDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Branch_branchID", nullable = false)
    private Branch branchBranchid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Status_statusID", nullable = false)
    private Status statusStatusid;

}