package com.example.stockmanagementsystembackend.domain.inventory;

import com.example.stockmanagementsystembackend.domain.inventory.controller.InventoryDashboardController;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.inventory.service.InventoryDashboardService;
import com.example.stockmanagementsystembackend.domain.stock.entity.Stock;
import com.example.stockmanagementsystembackend.domain.stock.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventoryDashboardTest {
    private InventoryItemRepository inventoryItems;
    private StockRepository stocks;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        inventoryItems = mock(InventoryItemRepository.class);
        stocks = mock(StockRepository.class);
        Clock clock = Clock.fixed(Instant.parse("2026-09-21T09:00:00Z"), ZoneOffset.UTC);
        InventoryDashboardService service = new InventoryDashboardService(inventoryItems, stocks, clock);
        mvc = MockMvcBuilders.standaloneSetup(new InventoryDashboardController(service)).build();
    }

    @Test
    void dashboardCalculatesInventoryOnlySummaryAndUsesExpectedExpiryWindow() throws Exception {
        InventoryItem rice = item(10.5, 250.0);
        InventoryItem soap = item(4.5, 100.0);
        InventoryItem itemWithMissingValues = item(null, null);
        List<InventoryItem> lowStock = List.of(rice, soap);
        List<Stock> expired = List.of(new Stock(), new Stock());
        List<Stock> expiring = List.of(new Stock(), new Stock(), new Stock());

        when(inventoryItems.findAll()).thenReturn(List.of(rice, soap, itemWithMissingValues));
        when(inventoryItems.findLowStock()).thenReturn(lowStock);
        when(stocks.findByExpiryDateBeforeOrderByExpiryDateAscStockIdAsc(LocalDate.of(2026, 9, 21)))
                .thenReturn(expired);
        when(stocks.findByExpiryDateBetweenOrderByExpiryDateAscStockIdAsc(
                LocalDate.of(2026, 9, 21), LocalDate.of(2026, 10, 21)))
                .thenReturn(expiring);

        mvc.perform(get("/api/inventory/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.totalQuantity").value(15.0))
                .andExpect(jsonPath("$.lowStockItems").value(2))
                .andExpect(jsonPath("$.expiredBatches").value(2))
                .andExpect(jsonPath("$.expiringSoonBatches").value(3))
                .andExpect(jsonPath("$.totalInventoryValue").value(3075.0));

        verify(inventoryItems).findAll();
        verify(inventoryItems).findLowStock();
        verify(stocks).findByExpiryDateBeforeOrderByExpiryDateAscStockIdAsc(LocalDate.of(2026, 9, 21));
        verify(stocks).findByExpiryDateBetweenOrderByExpiryDateAscStockIdAsc(
                LocalDate.of(2026, 9, 21), LocalDate.of(2026, 10, 21));
    }

    private InventoryItem item(Double quantity, Double unitPrice) {
        InventoryItem item = new InventoryItem();
        item.setTotalQuantity(quantity);
        item.setUnitPrice(unitPrice);
        return item;
    }
}
