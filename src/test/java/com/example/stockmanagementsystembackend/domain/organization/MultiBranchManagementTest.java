package com.example.stockmanagementsystembackend.domain.organization;

import com.example.stockmanagementsystembackend.domain.organization.controller.BranchController;
import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import com.example.stockmanagementsystembackend.domain.organization.repository.BranchRepository;
import com.example.stockmanagementsystembackend.domain.organization.repository.DepartmentRepository;
import com.example.stockmanagementsystembackend.domain.organization.service.BranchService;
import com.example.stockmanagementsystembackend.domain.stock.entity.Stock;
import com.example.stockmanagementsystembackend.domain.stock.repository.StockRepository;
import com.example.stockmanagementsystembackend.domain.stock.repository.StockTransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MultiBranchManagementTest {
    private BranchRepository branches;
    private DepartmentRepository departments;
    private StockRepository stocks;
    private StockTransferRepository transfers;
    private MockMvc mvc;
    private Branch branch;

    @BeforeEach
    void setUp() {
        branches = mock(BranchRepository.class);
        departments = mock(DepartmentRepository.class);
        stocks = mock(StockRepository.class);
        transfers = mock(StockTransferRepository.class);
        mvc = MockMvcBuilders.standaloneSetup(new BranchController(new BranchService(
                branches, departments, stocks, transfers))).build();

        branch = new Branch();
        branch.setId(2);
        branch.setName("Kandy Branch");
        branch.setLocation("Kandy");
        when(branches.findById(2)).thenReturn(Optional.of(branch));
        when(departments.findByBranch(branch)).thenReturn(List.of());
        when(transfers.countByDestinationBranch(branch)).thenReturn(4L);
        when(transfers.countBySourceBranch(branch)).thenReturn(3L);
        when(transfers.countByDestinationBranchAndStatus_NameIgnoreCase(branch, "Pending")).thenReturn(2L);
        when(transfers.countBySourceBranchAndStatus_NameIgnoreCase(branch, "Pending")).thenReturn(1L);
    }

    @Test
    void branchInventoryReturnsStockBatches() throws Exception {
        Stock first = stock(1001, 4500D);
        Stock second = stock(1002, 800D);
        when(stocks.findByBranch(branch)).thenReturn(List.of(first, second));

        mvc.perform(get("/api/branches/2/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockId").value(1001))
                .andExpect(jsonPath("$[0].branchName").value("Kandy Branch"))
                .andExpect(jsonPath("$[1].quantity").value(800));
    }

    @Test
    void branchPerformanceReportsInventoryAndTransferMetrics() throws Exception {
        when(stocks.findByBranch(branch)).thenReturn(List.of(stock(1001, 4500D), stock(1002, 800D)));

        mvc.perform(get("/api/branches/2/performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branchId").value(2))
                .andExpect(jsonPath("$.stockBatchCount").value(2))
                .andExpect(jsonPath("$.totalStockQuantity").value(5300))
                .andExpect(jsonPath("$.incomingTransferCount").value(4))
                .andExpect(jsonPath("$.outgoingTransferCount").value(3))
                .andExpect(jsonPath("$.pendingIncomingTransferCount").value(2))
                .andExpect(jsonPath("$.pendingOutgoingTransferCount").value(1));
    }

    private Stock stock(Integer id, Double quantity) {
        Stock stock = new Stock();
        stock.setId(id);
        stock.setQuantity(quantity);
        stock.setBranch(branch);
        return stock;
    }
}