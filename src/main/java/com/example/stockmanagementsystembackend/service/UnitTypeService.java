package com.example.stockmanagementsystembackend.service;

import com.example.stockmanagementsystembackend.entity.Unittype;
import com.example.stockmanagementsystembackend.repository.UnitTypeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UnitTypeService {
    private final UnitTypeRepository unitTypeRepository;

    public UnitTypeService(UnitTypeRepository unitTypeRepository) {
        this.unitTypeRepository = unitTypeRepository;
    }

    @Transactional
    public Unittype createUnitType(Unittype unitType) {
        validateUnitType(unitType);
        unitType.setId(null);
        return unitTypeRepository.save(unitType);
    }

    @Transactional(readOnly = true)
    public List<Unittype> getAllUnitTypes() {
        return unitTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Unittype getUnitTypeById(Integer id) {
        return unitTypeRepository.findById(id)
                .orElseThrow(() -> unitTypeNotFound(id));
    }

    @Transactional
    public Unittype updateUnitType(Integer id, Unittype unitType) {
        validateUnitType(unitType);

        Unittype existingUnitType = unitTypeRepository.findById(id)
                .orElseThrow(() -> unitTypeNotFound(id));
        existingUnitType.setUnit(unitType.getUnit());

        return unitTypeRepository.save(existingUnitType);
    }

    @Transactional
    public void deleteUnitType(Integer id) {
        Unittype unitType = unitTypeRepository.findById(id)
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

    private void validateUnitType(Unittype unitType) {
        if (unitType == null || unitType.getUnit() == null || unitType.getUnit().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unit must not be blank");
        }

        if (unitType.getUnit().length() > 45) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "unit must not exceed 45 characters"
            );
        }
    }

    private ResponseStatusException unitTypeNotFound(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Unit type with ID " + id + " was not found");
    }
}
