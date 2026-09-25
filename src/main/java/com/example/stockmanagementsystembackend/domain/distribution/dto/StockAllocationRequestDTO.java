package com.example.stockmanagementsystembackend.domain.distribution.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StockAllocationRequestDTO {

    private List<StockAllocationDTO> items;
}
