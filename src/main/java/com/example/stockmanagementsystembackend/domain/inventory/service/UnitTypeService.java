package com.example.stockmanagementsystembackend.domain.inventory.service;

import com.example.stockmanagementsystembackend.domain.inventory.entity.UnitType;
import com.example.stockmanagementsystembackend.domain.inventory.repository.UnitTypeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
// Polymorphism: callers may depend on the generic CRUD contract.
public class UnitTypeService implements CrudService<UnitType, Integer> {
    private final UnitTypeRepository unitTypeRepository;

    public UnitTypeService(UnitTypeRepository unitTypeRepository) {
        this.unitTypeRepository = unitTypeRepository;
    }

    @Transactional
    @Override
    public UnitType create(UnitType unitType) {
        validateUnitType(unitType);
        unitType.setId(null);
        return unitTypeRepository.save(unitType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnitType> getAll() {
        return unitTypeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UnitType getById(Integer id) {
        return unitTypeRepository.findById(id)
                .orElseThrow(() -> unitTypeNotFound(id));
    }

    @Override
    @Transactional
    public UnitType update(Integer id, UnitType unitType) {
        validateUnitType(unitType);

        UnitType existingUnitType = unitTypeRepository.findById(id)
                .orElseThrow(() -> unitTypeNotFound(id));
        existingUnitType.setName(unitType.getName());

        return unitTypeRepository.save(existingUnitType);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        UnitType unitType = unitTypeRepository.findById(id)
                .orElseThrow(() -> unitTypeNotFound(id));

        try {
            unitTypeRepository.delete(unitType);
            unitTypeRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unit type cannot be deleted because it is in use"
            );
        }
    }

    private void validateUnitType(UnitType unitType) {
        if (unitType == null || unitType.getName() == null || unitType.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name must not be blank");
        }

        if (unitType.getName().length() > 45) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "name must not exceed 45 characters"
            );
        }
    }

    private ResponseStatusException unitTypeNotFound(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Unit type with ID " + id + " was not found");
    }
}
