package com.example.stockmanagementsystembackend.domain.stock.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class StockTransferCreateRequest {
    @NotNull
    private Integer fromBranchId;
    @NotNull
    private Integer toBranchId;
    @NotNull
    private Integer requestedById;
    @NotNull
    private Integer statusId;
    @NotEmpty
    @Valid
    private List<TransferItemDto> items;
}