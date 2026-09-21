package com.example.stockmanagementsystembackend.domain.inventory;

import com.example.stockmanagementsystembackend.domain.inventory.controller.CategoryController;
import com.example.stockmanagementsystembackend.domain.inventory.controller.UnitTypeController;
import com.example.stockmanagementsystembackend.domain.inventory.entity.Category;
import com.example.stockmanagementsystembackend.domain.inventory.entity.UnitType;
import com.example.stockmanagementsystembackend.domain.inventory.repository.CategoryRepository;
import com.example.stockmanagementsystembackend.domain.inventory.repository.UnitTypeRepository;
import com.example.stockmanagementsystembackend.domain.inventory.service.CategoryService;
import com.example.stockmanagementsystembackend.domain.inventory.service.UnitTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryUnitTypeCrudTest {
    private CategoryRepository categories;
    private UnitTypeRepository units;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        categories = mock(CategoryRepository.class);
        units = mock(UnitTypeRepository.class);
        mvc = MockMvcBuilders.standaloneSetup(
                new CategoryController(new CategoryService(categories)),
                new UnitTypeController(new UnitTypeService(units))).build();
    }

    private String route(String resource) { return "/api/" + resource; }
    private String idField(String resource) {
        return resource.equals("categories") ? "categoryId" : "unitTypeId";
    }

    private void existingRecords() {
        Category category = new Category(7, "Food", "Description");
        UnitType unit = new UnitType(7, "Kilogram");
        when(categories.findById(7)).thenReturn(Optional.of(category));
        when(units.findById(7)).thenReturn(Optional.of(unit));
        when(categories.findAll()).thenReturn(List.of(category));
        when(units.findAll()).thenReturn(List.of(unit));
        when(categories.save(any())).thenAnswer(call -> call.getArgument(0));
        when(units.save(any())).thenAnswer(call -> call.getArgument(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"categories", "unit-types"})
    void createTrimsNameAndUsesGeneratedIdInsteadOfClientId(String resource) throws Exception {
        when(categories.save(any())).thenAnswer(call -> {
            Category category = call.getArgument(0);
            assertNull(category.getCategoryId());
            category.setCategoryId(7);
            return category;
        });
        when(units.save(any())).thenAnswer(call -> {
            UnitType unit = call.getArgument(0);
            assertNull(unit.getUnitTypeId());
            unit.setUnitTypeId(7);
            return unit;
        });
        mvc.perform(post(route(resource)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"" + idField(resource) + "\":999,\"name\":\"  Example  \"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", route(resource) + "/7"))
                .andExpect(jsonPath("$." + idField(resource)).value(7))
                .andExpect(jsonPath("$.name").value("Example"))
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(strings = {"categories", "unit-types"})
    void listReadUpdateAndDeleteReturnExpectedStatuses(String resource) throws Exception {
        existingRecords();
        mvc.perform(get(route(resource))).andExpect(status().isOk())
                .andExpect(jsonPath("$[0]." + idField(resource)).value(7));
        mvc.perform(get(route(resource) + "/7")).andExpect(status().isOk())
                .andExpect(jsonPath("$." + idField(resource)).value(7));
        mvc.perform(put(route(resource) + "/7").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"" + idField(resource) + "\":999,\"name\":\" Updated \"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + idField(resource)).value(7))
                .andExpect(jsonPath("$.name").value("Updated"));
        mvc.perform(delete(route(resource) + "/7")).andExpect(status().isNoContent());
        if (resource.equals("categories")) {
            verify(categories).delete(any(Category.class));
            verify(categories).flush();
        } else {
            verify(units).delete(any(UnitType.class));
            verify(units).flush();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"categories", "unit-types"})
    void absentRecordsReturn404(String resource) throws Exception {
        mvc.perform(get(route(resource) + "/999")).andExpect(status().isNotFound());
        mvc.perform(put(route(resource) + "/999").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Valid\"}")).andExpect(status().isNotFound());
        mvc.perform(delete(route(resource) + "/999")).andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"categories", "unit-types"})
    void invalidNamesReturn400WithoutPersistence(String resource) throws Exception {
        for (String body : List.of("{}", "{\"name\":null}", "{\"name\":\"   \"}",
                "{\"name\":\"" + "x".repeat(46) + "\"}")) {
            mvc.perform(post(route(resource)).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
            mvc.perform(put(route(resource) + "/7").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(categories, units);
    }

    @ParameterizedTest
    @ValueSource(strings = {"categories", "unit-types"})
    void referencedRecordDeletionReturns409(String resource) throws Exception {
        existingRecords();
        doThrow(new DataIntegrityViolationException("foreign key")).when(categories).flush();
        doThrow(new DataIntegrityViolationException("foreign key")).when(units).flush();
        mvc.perform(delete(route(resource) + "/7")).andExpect(status().isConflict());
    }

    @ParameterizedTest
    @ValueSource(strings = {"categories", "unit-types"})
    void searchTrimsKeywordAndRoutesToExistingNameQuery(String resource) throws Exception {
        when(categories.findByNameContainingIgnoreCaseOrderByNameAsc("fOoD"))
                .thenReturn(List.of(new Category(7, "Food", null)));
        when(units.findByNameContainingIgnoreCaseOrderByNameAsc("fOoD"))
                .thenReturn(List.of(new UnitType(7, "Food")));
        mvc.perform(get(route(resource) + "/search").param("keyword", " fOoD "))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Food"));
    }
}
