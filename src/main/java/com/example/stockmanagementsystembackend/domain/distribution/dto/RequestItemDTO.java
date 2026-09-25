package com.example.stockmanagementsystembackend.domain.distribution.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestItemDTO {

    private Integer itemId;

    private Double quantity;
}