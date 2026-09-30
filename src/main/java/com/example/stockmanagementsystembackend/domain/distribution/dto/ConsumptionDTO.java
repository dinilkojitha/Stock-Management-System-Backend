package com.example.stockmanagementsystembackend.domain.distribution.dto;



import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumptionDTO {

    private Integer requestId;
    private Integer departmentId;
    private List<ConsumptionItemDTO> items;
}