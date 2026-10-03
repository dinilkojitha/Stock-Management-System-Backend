package com.example.stockmanagementsystembackend.domain.distribution.service;

import com.example.stockmanagementsystembackend.domain.distribution.dto.ConsumptionDTO;
import com.example.stockmanagementsystembackend.domain.distribution.dto.ConsumptionItemDTO;
import com.example.stockmanagementsystembackend.domain.distribution.dto.ConsumptionResponseDTO;
import com.example.stockmanagementsystembackend.domain.distribution.entity.Consumption;
import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItem;
import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItemId;
import com.example.stockmanagementsystembackend.domain.distribution.entity.Internalrequest;
import com.example.stockmanagementsystembackend.domain.distribution.repository.ConsumptionRepository;
import com.example.stockmanagementsystembackend.domain.distribution.repository.InternalRequestItemRepository;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.organization.entity.Department;
import com.example.stockmanagementsystembackend.domain.organization.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConsumptionService {

    private final ConsumptionRepository consumptionRepository;
    private final DepartmentRepository departmentRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InternalRequestItemRepository internalRequestItemRepository;

    public ConsumptionService(
            ConsumptionRepository consumptionRepository,
            DepartmentRepository departmentRepository,
            InventoryItemRepository inventoryItemRepository,
            InternalRequestItemRepository internalRequestItemRepository) {

        this.consumptionRepository = consumptionRepository;
        this.departmentRepository = departmentRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.internalRequestItemRepository = internalRequestItemRepository;
    }

    public List<ConsumptionResponseDTO> createConsumption(ConsumptionDTO dto) {

        List<Consumption> consumptions = new ArrayList<>();

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() ->
                        new RuntimeException("Department not found"));

        Internalrequest request = new Internalrequest();
        request.setId(dto.getRequestId());

        for (ConsumptionItemDTO itemDTO : dto.getItems()) {

            InventoryItem item = inventoryItemRepository.findById(itemDTO.getItemId())
                    .orElseThrow(() ->
                            new RuntimeException("Inventory item not found"));

            InternalRequestItemId requestItemId =
                    new InternalRequestItemId();

            requestItemId.setRequestId(dto.getRequestId());
            requestItemId.setItemId(itemDTO.getItemId());

            InternalRequestItem requestItem =
                    internalRequestItemRepository.findById(requestItemId)
                            .orElseThrow(() ->
                                    new RuntimeException("Request item not found"));

            Double allocated = requestItem.getAllocatedQuantity();

            if (allocated == null) {
                throw new RuntimeException(
                        "No stock has been allocated for this item"
                );
            }

            if (itemDTO.getQuantityConsumed() < 0) {
                throw new RuntimeException(
                        "Consumed quantity cannot be negative"
                );
            }

            if (itemDTO.getQuantityConsumed() > allocated) {
                throw new RuntimeException(
                        "Consumed quantity cannot exceed allocated quantity"
                );
            }

            Optional<Consumption> existingConsumption =
                    consumptionRepository.findByRequest_IdAndItem_Id(
                            dto.getRequestId(),
                            itemDTO.getItemId()
                    );

            if (existingConsumption.isPresent()) {
                throw new RuntimeException(
                        "Consumption record already exists for this request and item"
                );
            }

            Consumption consumption = new Consumption();

            consumption.setRequest(request);
            consumption.setDepartment(department);
            consumption.setItem(item);
            consumption.setQuantityConsumed(
                    itemDTO.getQuantityConsumed()
            );
            consumption.setConsumedAt(Instant.now());

            consumptions.add(consumption);
        }

        List<Consumption> savedConsumptions =
                consumptionRepository.saveAll(consumptions);

        return savedConsumptions.stream().map(consumption -> {

            ConsumptionResponseDTO response =
                    new ConsumptionResponseDTO();

            response.setConsumptionId(consumption.getId());
            response.setRequestId(
                    consumption.getRequest().getId()
            );
            response.setDepartmentId(
                    consumption.getDepartment().getId()
            );
            response.setItemId(
                    consumption.getItem().getId()
            );
            response.setQuantityConsumed(
                    consumption.getQuantityConsumed()
            );
            response.setConsumedAt(
                    consumption.getConsumedAt()
            );

            InternalRequestItemId requestItemId =
                    new InternalRequestItemId();

            requestItemId.setRequestId(
                    consumption.getRequest().getId()
            );

            requestItemId.setItemId(
                    consumption.getItem().getId()
            );

            InternalRequestItem requestItem =
                    internalRequestItemRepository.findById(requestItemId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Request item not found"
                                    ));

            response.setAllocatedQuantity(
                    requestItem.getAllocatedQuantity()
            );

            return response;

        }).toList();
    }

    public List<ConsumptionResponseDTO> getAllConsumptions() {

        List<Consumption> consumptions =
                consumptionRepository.findAll();

        return consumptions.stream().map(consumption -> {

            ConsumptionResponseDTO dto =
                    new ConsumptionResponseDTO();

            dto.setConsumptionId(
                    consumption.getId()
            );

            dto.setRequestId(
                    consumption.getRequest().getId()
            );

            dto.setDepartmentId(
                    consumption.getDepartment().getId()
            );

            dto.setItemId(
                    consumption.getItem().getId()
            );

            dto.setQuantityConsumed(
                    consumption.getQuantityConsumed()
            );

            dto.setConsumedAt(
                    consumption.getConsumedAt()
            );

            InternalRequestItemId requestItemId =
                    new InternalRequestItemId();

            requestItemId.setRequestId(
                    consumption.getRequest().getId()
            );

            requestItemId.setItemId(
                    consumption.getItem().getId()
            );

            InternalRequestItem requestItem =
                    internalRequestItemRepository.findById(requestItemId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Request item not found"
                                    ));

            dto.setAllocatedQuantity(
                    requestItem.getAllocatedQuantity()
            );

            return dto;

        }).toList();
    }

    public Consumption updateConsumption(
            Integer id,
            Double quantityConsumed) {

        Consumption consumption =
                consumptionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Consumption not found"
                                ));

        InternalRequestItemId requestItemId =
                new InternalRequestItemId();

        requestItemId.setRequestId(
                consumption.getRequest().getId()
        );

        requestItemId.setItemId(
                consumption.getItem().getId()
        );

        InternalRequestItem requestItem =
                internalRequestItemRepository.findById(requestItemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Request item not found"
                                ));

        Double allocated =
                requestItem.getAllocatedQuantity();

        if (allocated == null) {
            throw new RuntimeException(
                    "No stock has been allocated for this item"
            );
        }

        if (quantityConsumed < 0) {
            throw new RuntimeException(
                    "Consumed quantity cannot be negative"
            );
        }

        Double currentConsumed =
                consumption.getQuantityConsumed();

        Double newTotalConsumed =
                currentConsumed + quantityConsumed;

        if (newTotalConsumed > allocated) {
            throw new RuntimeException(
                    "Consumption cannot exceed allocated quantity"
            );
        }

        consumption.setQuantityConsumed(
                newTotalConsumed
        );

        return consumptionRepository.save(consumption);
    }
}