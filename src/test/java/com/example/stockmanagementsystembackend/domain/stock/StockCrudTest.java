package com.example.stockmanagementsystembackend.domain.stock;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import com.example.stockmanagementsystembackend.domain.organization.repository.BranchRepository;
import com.example.stockmanagementsystembackend.domain.stock.controller.StockController;
import com.example.stockmanagementsystembackend.domain.stock.entity.Stock;
import com.example.stockmanagementsystembackend.domain.stock.repository.StockRepository;
import com.example.stockmanagementsystembackend.domain.stock.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StockCrudTest {
    private StockRepository stocks;
    private BranchRepository branches;
    private InventoryItemRepository items;
    private MockMvc mvc;
    private StockService service;

    @BeforeEach
    void setUp() {
        stocks = mock(StockRepository.class);
        branches = mock(BranchRepository.class);
        items = mock(InventoryItemRepository.class);
        Clock clock = Clock.fixed(Instant.parse("2026-09-21T09:00:00Z"), ZoneOffset.UTC);
        service = new StockService(stocks, branches, items, clock);
        mvc = MockMvcBuilders.standaloneSetup(new StockController(service)).build();
        Branch branch = new Branch();
        branch.setId(1);
        when(branches.findById(1)).thenReturn(Optional.of(branch));
        InventoryItem item = new InventoryItem();
        item.setId(7);
        when(items.findAllById(Set.of(7))).thenReturn(List.of(item));
        when(stocks.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));
    }

    private String json() {
        return """
                {"stockId":500,"quantity":20,"branchId":1,"itemIds":[7],
                 "manufactureDate":"2026-09-01","expiryDate":"2026-10-01"}
                """;
    }

    private Stock stock() {
        Stock stock = new Stock();
        stock.setStockId(500);
        stock.setBranchId(1);
        stock.setItemIds(Set.of(7));
        stock.setQuantity(20.0);
        return stock;
    }

    @Test
    void postUsesSuppliedIdAndFlatJsonWithoutRecursion() throws Exception {
        mvc.perform(post("/api/stocks").contentType(MediaType.APPLICATION_JSON).content(json()))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/stocks/500"))
                .andExpect(jsonPath("$.stockId").value(500)).andExpect(jsonPath("$.branchId").value(1))
                .andExpect(jsonPath("$.itemIds[0]").value(7)).andExpect(jsonPath("$.quantity").value(20))
                .andExpect(jsonPath("$.expiryDate").value("2026-10-01"))
                .andExpect(jsonPath("$.branch").doesNotExist()).andExpect(jsonPath("$.items").doesNotExist())
                .andExpect(jsonPath("$.new").doesNotExist()).andExpect(jsonPath("$.id").doesNotExist());
        verify(items, never()).save(any());
        verify(branches, never()).save(any());
        assertTrue(stock().isNew(), "Assigned IDs must use INSERT, never accidental upsert");
    }

    @Test
    void missingIdIsRejectedAndDuplicateIdReturnsConflict() throws Exception {
        mvc.perform(post("/api/stocks").contentType(MediaType.APPLICATION_JSON)
                        .content(json().replace("\"stockId\":500,", "")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.detail").value(
                        "stockId is required; Stock.stock_id is not auto-increment"));
        when(stocks.existsById(500)).thenReturn(true);
        mvc.perform(post("/api/stocks").contentType(MediaType.APPLICATION_JSON).content(json()))
                .andExpect(status().isConflict());
        verify(stocks, never()).saveAndFlush(any());
    }

    @Test
    void duplicateIdRaceIsAlsoReportedAsConflict() throws Exception {
        when(stocks.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("Duplicate primary key"));
        mvc.perform(post("/api/stocks").contentType(MediaType.APPLICATION_JSON).content(json()))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.detail").exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "null", "1e309"})
    void invalidQuantityIsRejected(String quantity) throws Exception {
        mvc.perform(post("/api/stocks").contentType(MediaType.APPLICATION_JSON)
                        .content(json().replace("\"quantity\":20", "\"quantity\":" + quantity)))
                .andExpect(status().isBadRequest());
        verify(stocks, never()).saveAndFlush(any());
    }

    @Test
    void invertedDatesMissingBranchAndInvalidItemsAreRejected() throws Exception {
        for (String invalid : List.of(
                json().replace("2026-10-01", "2026-08-31"),
                json().replace("\"branchId\":1", "\"branchId\":null"),
                json().replace("\"branchId\":1", "\"branchId\":999"),
                json().replace("[7]", "[999]"),
                json().replace("[7]", "[null]"),
                json().replace("[7]", "null"))) {
            mvc.perform(post("/api/stocks").contentType(MediaType.APPLICATION_JSON).content(invalid))
                    .andExpect(status().isBadRequest());
        }
        verify(stocks, never()).saveAndFlush(any());
    }

    @Test
    void zeroQuantityEqualDatesAndDuplicateItemIdsAreAccepted() throws Exception {
        mvc.perform(post("/api/stocks").contentType(MediaType.APPLICATION_JSON)
                        .content(json().replace("\"quantity\":20", "\"quantity\":0")
                                .replace("2026-10-01", "2026-09-01").replace("[7]", "[7,7]")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.itemIds.length()").value(1));
    }

    @Test
    void updateUsesPathIdAndReplacesBatchValues() throws Exception {
        when(stocks.findByIdForUpdate(500)).thenReturn(Optional.of(stock()));
        mvc.perform(put("/api/stocks/500").contentType(MediaType.APPLICATION_JSON)
                        .content(json().replace("\"stockId\":500,", "").replace("\"quantity\":20", "\"quantity\":30")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.stockId").value(500))
                .andExpect(jsonPath("$.quantity").value(30));
        mvc.perform(put("/api/stocks/500").contentType(MediaType.APPLICATION_JSON)
                        .content(json().replace("\"stockId\":500", "\"stockId\":501")))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"get", "put", "delete"})
    void missingBatchReturns404(String method) throws Exception {
        var request = switch (method) {
            case "put" -> put("/api/stocks/500").contentType(MediaType.APPLICATION_JSON).content(json());
            case "delete" -> delete("/api/stocks/500");
            default -> get("/api/stocks/500");
        };
        mvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    void readAndFilterRoutesReturnStock() throws Exception {
        when(stocks.findAll()).thenReturn(List.of(stock()));
        when(stocks.findById(500)).thenReturn(Optional.of(stock()));
        when(branches.existsById(1)).thenReturn(true);
        when(items.existsById(7)).thenReturn(true);
        when(stocks.findByBranch_IdOrderByStockIdAsc(1)).thenReturn(List.of(stock()));
        when(stocks.findDistinctByItems_IdOrderByStockIdAsc(7)).thenReturn(List.of(stock()));
        for (String path : List.of("", "/branch/1", "/item/7")) {
            mvc.perform(get("/api/stocks" + path)).andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].stockId").value(500));
        }
        mvc.perform(get("/api/stocks/500")).andExpect(status().isOk());
        mvc.perform(get("/api/stocks/branch/999")).andExpect(status().isNotFound());
        mvc.perform(get("/api/stocks/item/999")).andExpect(status().isNotFound());
    }

    @Test
    void expiryEndpointsUseInclusiveWindowAndRejectInvalidDays() throws Exception {
        LocalDate today = LocalDate.of(2026, 9, 21);
        when(stocks.findByExpiryDateBetweenOrderByExpiryDateAscStockIdAsc(today, today.plusDays(30)))
                .thenReturn(List.of(stock()));
        mvc.perform(get("/api/stocks/expiring")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockId").value(500));
        mvc.perform(get("/api/stocks/expiring?days=0")).andExpect(status().isOk());
        verify(stocks).findByExpiryDateBetweenOrderByExpiryDateAscStockIdAsc(today, today);
        mvc.perform(get("/api/stocks/expired")).andExpect(status().isOk());
        verify(stocks).findByExpiryDateBeforeOrderByExpiryDateAscStockIdAsc(today);
        for (String days : List.of("-1", "abc", "2147483647")) {
            mvc.perform(get("/api/stocks/expiring?days=" + days)).andExpect(status().isBadRequest());
        }
    }

    @Test
    void deleteReturns204OrUsefulConflict() throws Exception {
        when(stocks.findByIdForUpdate(500)).thenReturn(Optional.of(stock()));
        mvc.perform(delete("/api/stocks/500")).andExpect(status().isNoContent());
        doThrow(new DataIntegrityViolationException("Referenced batch")).when(stocks).flush();
        mvc.perform(delete("/api/stocks/500")).andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Stock cannot be deleted because another record references it"));
        verify(items, never()).delete(any());
        verify(branches, never()).delete(any());
    }
}
