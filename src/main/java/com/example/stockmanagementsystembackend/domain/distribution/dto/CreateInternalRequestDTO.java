package com.example.stockmanagementsystembackend.domain.distribution.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class CreateInternalRequestDTO {

    private Integer departmentId;

    private Integer requestedByUserId;

    private Instant requestedAt;

    private Boolean emergencyRequest = false;

    private String status;

    private List<RequestItemDTO> items;
}