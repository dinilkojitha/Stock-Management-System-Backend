package com.example.stockmanagementsystembackend.domain.inventory;

import com.example.stockmanagementsystembackend.domain.inventory.controller.InventoryItemController;
import com.example.stockmanagementsystembackend.domain.inventory.entity.Category;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.entity.UnitType;
import com.example.stockmanagementsystembackend.domain.inventory.repository.CategoryRepository;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.inventory.repository.UnitTypeRepository;
import com.example.stockmanagementsystembackend.domain.inventory.service.InventoryItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InventoryItemCrudTest {
    private InventoryItemRepository items;
    private CategoryRepository categories;
    private UnitTypeRepository units;
    private InventoryItemService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        items = mock(InventoryItemRepository.class);
        categories = mock(CategoryRepository.class);
        units = mock(UnitTypeRepository.class);
        service = new InventoryItemService(items, categories, units);
        mvc = MockMvcBuilders.standaloneSetup(new InventoryItemController(service)).build();
        when(categories.findById(1)).thenReturn(Optional.of(new Category(1, "Food", null)));
        when(units.findById(2)).thenReturn(Optional.of(new UnitType(2, "Kilogram")));
        when(items.save(any())).thenAnswer(call -> {
            InventoryItem item = call.getArgument(0);
            if (item.getId() == null) item.setId(7);
            return item;
        });
    }

    private InventoryItem item() {
        return new InventoryItem(7, "Rice", new Category(1, "Food", null), 20.0,
                new UnitType(2, "Kilogram"), 250.0, "White rice", 10.0);
    }

    private String json() {
        return """
                {"name":"  Rice  ","categoryId":1,"unitTypeId":2,"totalQuantity":20,
                 "unitPrice":250,"description":"White rice","reorderThreshold":10}
                """;
    }

    @Test
    void postAcceptsFlatForeignKeysAndReturnsCreatedWithoutLazyObjects() throws Exception {
        mvc.perform(post("/api/inventory-items").contentType(MediaType.APPLICATION_JSON).content(json()))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/inventory-items/7"))
                .andExpect(jsonPath("$.name").value("Rice"))
                .andExpect(jsonPath("$.categoryId").value(1)).andExpect(jsonPath("$.unitTypeId").value(2))
                .andExpect(jsonPath("$.category").doesNotExist()).andExpect(jsonPath("$.unitType").doesNotExist())
                .andExpect(jsonPath("$.itemName").doesNotExist());
    }

    @Test
    void putUpdatesExistingIdAndAllFields() throws Exception {
        when(items.findByIdForUpdate(7)).thenReturn(Optional.of(item()));
        mvc.perform(put("/api/inventory-items/7").contentType(MediaType.APPLICATION_JSON)
                        .content(json().replace("20", "30")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.totalQuantity").value(30));
        verify(items).findByIdForUpdate(7);
    }

    @ParameterizedTest
    @ValueSource(strings = {"get", "put", "delete", "quantity"})
    void missingItemReturns404(String operation) throws Exception {
        var request = switch (operation) {
            case "put" -> put("/api/inventory-items/999").contentType(MediaType.APPLICATION_JSON).content(json());
            case "delete" -> delete("/api/inventory-items/999");
            case "quantity" -> put("/api/inventory-items/999/quantity")
                    .contentType(MediaType.APPLICATION_JSON).content("{\"quantityDelta\":10}");
            default -> get("/api/inventory-items/999");
        };
        mvc.perform(request).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Inventory item with ID 999 was not found"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"\"", "\"   \"", "null", "\"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789\""})
    void invalidNamesReturn400(String name) throws Exception {
        mvc.perform(post("/api/inventory-items").contentType(MediaType.APPLICATION_JSON)
                        .content(json().replace("\"  Rice  \"", name)))
                .andExpect(status().isBadRequest());
        verify(items, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"categoryId", "unitTypeId"})
    void requiredAndUnknownForeignKeysAreRejected(String field) throws Exception {
        String oldValue = field.equals("categoryId") ? "1" : "2";
        String original = "\"" + field + "\":" + oldValue;
        for (String invalid : List.of("null", "999")) {
            mvc.perform(post("/api/inventory-items").contentType(MediaType.APPLICATION_JSON)
                            .content(json().replace(original, "\"" + field + "\":" + invalid)))
                    .andExpect(status().isBadRequest());
        }
        verify(items, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"totalQuantity", "unitPrice", "reorderThreshold"})
    void negativeNumbersRejectedForPostAndPut(String field) throws Exception {
        String invalid = json().replaceAll("\"" + field + "\":\\d+", "\"" + field + "\":-1");
        mvc.perform(post("/api/inventory-items").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/api/inventory-items/7").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest());
        verify(items, never()).save(any());
    }

    @Test
    void listSearchCategoryAndLowStockRoutesReturnItems() throws Exception {
        when(items.findAll()).thenReturn(List.of(item()));
        when(items.findById(7)).thenReturn(Optional.of(item()));
        when(items.findByNameContainingIgnoreCaseOrderByNameAsc("rice")).thenReturn(List.of(item()));
        when(categories.existsById(1)).thenReturn(true);
        when(items.findByCategory_CategoryIdOrderByNameAsc(1)).thenReturn(List.of(item()));
        when(items.findLowStock()).thenReturn(List.of(item()));
        for (String path : List.of("", "/search?keyword=rice", "/category/1", "/low-stock")) {
            mvc.perform(get("/api/inventory-items" + path)).andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(7));
        }
        mvc.perform(get("/api/inventory-items/7")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(7));
        mvc.perform(get("/api/inventory-items/category/999")).andExpect(status().isNotFound());
    }

    @Test
    void referencedDeleteReturnsUsefulConflictAndSuccessfulDeleteReturns204() throws Exception {
        when(items.findById(7)).thenReturn(Optional.of(item()));
        mvc.perform(delete("/api/inventory-items/7")).andExpect(status().isNoContent());
        doThrow(new DataIntegrityViolationException("Foreign key reference")).when(items).flush();
        mvc.perform(delete("/api/inventory-items/7")).andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Inventory item cannot be deleted because it is in use"));
    }

    @Test
    void quantityCanReceiveIssueAndReachZeroWithoutChangingOtherFields() throws Exception {
        InventoryItem existing = item();
        when(items.findByIdForUpdate(7)).thenReturn(Optional.of(existing));
        for (double delta : new double[]{10, -5, -25}) {
            mvc.perform(put("/api/inventory-items/7/quantity").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"quantityDelta\":" + delta + "}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Rice"))
                    .andExpect(jsonPath("$.unitPrice").value(250));
        }
        assertEquals(0.0, existing.getTotalQuantity());
        verify(items, times(3)).findByIdForUpdate(7);
        verifyNoInteractions(categories, units);
    }

    @Test
    void overdraftAndMissingDeltaAreRejectedWithoutSaving() throws Exception {
        InventoryItem existing = item();
        when(items.findByIdForUpdate(7)).thenReturn(Optional.of(existing));
        for (String body : List.of("{\"quantityDelta\":-21}", "{}", "{\"quantityDelta\":null}")) {
            mvc.perform(put("/api/inventory-items/7/quantity").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        assertEquals(20.0, existing.getTotalQuantity());
        verify(items, never()).save(any());
    }

    @Test
    void nonFiniteNumbersAndOverflowAreRejected() {
        for (double invalid : new double[]{Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            InventoryItem request = item();
            request.setUnitPrice(invalid);
            assertThrows(ResponseStatusException.class, () -> service.save(request));
            assertThrows(ResponseStatusException.class, () -> service.adjustQuantity(7, invalid));
        }
        InventoryItem existing = item();
        existing.setTotalQuantity(Double.MAX_VALUE);
        when(items.findByIdForUpdate(7)).thenReturn(Optional.of(existing));
        assertThrows(ResponseStatusException.class, () -> service.adjustQuantity(7, Double.MAX_VALUE));
        verify(items, never()).save(any());
    }

    @Test
    void valuationIsCalculatedAndLegacyNullQuantityCanReceive() {
        InventoryItem existing = item();
        assertEquals(5000.0, existing.calculateValuation());
        existing.setTotalQuantity(null);
        assertEquals(0.0, existing.calculateValuation());
        when(items.findByIdForUpdate(7)).thenReturn(Optional.of(existing));
        assertEquals(10.0, service.adjustQuantity(7, 10.0).getTotalQuantity());
    }
}
