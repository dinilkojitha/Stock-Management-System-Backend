package com.example.stockmanagementsystembackend.domain.inventory.controller;

import com.example.stockmanagementsystembackend.domain.inventory.entity.UnitType;
import com.example.stockmanagementsystembackend.domain.inventory.service.UnitTypeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/unit-types")
public class UnitTypeController {
    private final UnitTypeService unitTypeService;

    public UnitTypeController(UnitTypeService unitTypeService) {
        this.unitTypeService = unitTypeService;
    }

    @PostMapping
    public ResponseEntity<UnitType> createUnitType(@Valid @RequestBody UnitType unitType) {
        UnitType createdUnitType = unitTypeService.createUnitType(unitType);
        URI location = URI.create("/api/unit-types/" + createdUnitType.getId());
        return ResponseEntity.created(location).body(createdUnitType);
    }

    @GetMapping
    public ResponseEntity<List<UnitType>> getAllUnitTypes() {
        return ResponseEntity.ok(unitTypeService.getAllUnitTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitType> getUnitTypeById(@PathVariable Integer id) {
        return ResponseEntity.ok(unitTypeService.getUnitTypeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnitType> updateUnitType(
            @PathVariable Integer id,
            @Valid @RequestBody UnitType unitType
    ) {
        return ResponseEntity.ok(unitTypeService.updateUnitType(id, unitType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnitType(@PathVariable Integer id) {
        unitTypeService.deleteUnitType(id);
        return ResponseEntity.noContent().build();
    }

}
