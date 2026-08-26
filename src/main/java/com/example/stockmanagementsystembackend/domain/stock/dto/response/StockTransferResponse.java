package com.example.stockmanagementsystembackend.domain.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter @AllArgsConstructor
public class StockTransferResponse {
    private Integer id;
    private Integer fromBranchId;
    private String fromBranchName;
    private Integer toBranchId;
    private String toBranchName;
    private Integer requestedById;
    private String requestedByName;
    private Integer statusId;
    private String statusName;
    private String requestTime;
    private List<TransferItemResponseDto> items;
}