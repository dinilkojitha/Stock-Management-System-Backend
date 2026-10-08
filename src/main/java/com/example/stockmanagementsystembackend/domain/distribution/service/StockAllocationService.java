package com.example.stockmanagementsystembackend.domain.distribution.service;

import com.example.stockmanagementsystembackend.domain.distribution.repository.InternalRequestRepository;
import com.example.stockmanagementsystembackend.domain.distribution.repository.InternalRequestItemRepository;
import com.example.stockmanagementsystembackend.domain.distribution.dto.StockAllocationRequestDTO;
import com.example.stockmanagementsystembackend.domain.stock.dto.request.StockIssueRequest;
import com.example.stockmanagementsystembackend.domain.stock.service.StockMovementService;
import com.example.stockmanagementsystembackend.domain.distribution.entity.Internalrequest;
import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItem;
import com.example.stockmanagementsystembackend.domain.distribution.dto.StockAllocationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockAllocationService {

    private final InternalRequestRepository internalRequestRepository;
    private final InternalRequestItemRepository internalRequestItemRepository;

    private final StockMovementService stockMovementService;

    public StockAllocationService(
            InternalRequestRepository internalRequestRepository,
            InternalRequestItemRepository internalRequestItemRepository,
            StockMovementService stockMovementService) {

        this.internalRequestRepository = internalRequestRepository;
        this.internalRequestItemRepository = internalRequestItemRepository;
        this.stockMovementService = stockMovementService;
    }
    @Transactional
    public void allocateStock(
            Integer requestId,
            StockAllocationRequestDTO allocationRequest) {

        Internalrequest request = internalRequestRepository.findByIdForUpdate(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        if (allocationRequest.getAllocatedByUserId() == null || allocationRequest.getItems() == null
                || allocationRequest.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An allocator and at least one item are required");
        }

        Map<Integer, InternalRequestItem> requestItems = new HashMap<>();
        for (InternalRequestItem item : internalRequestItemRepository.findByIdRequestId(requestId)) {
            requestItems.put(item.getId().getItemId(), item);
        }
        Integer branchId = request.getDepartment().getBranch().getId();
        Map<Integer, Boolean> processedItems = new HashMap<>();
        for (StockAllocationDTO allocation : allocationRequest.getItems()) {
            if (allocation.getItemId() == null || allocation.getQuantity() == null
                    || !Double.isFinite(allocation.getQuantity()) || allocation.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Allocation quantities must be positive");
            }
            if (processedItems.put(allocation.getItemId(), Boolean.TRUE) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An item can only be allocated once per request");
            }

            InternalRequestItem requestItem = requestItems.get(allocation.getItemId());
            if (requestItem == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is not part of this request");
            }
            double allocatedQuantity = requestItem.getAllocatedQuantity() == null
                    ? 0 : requestItem.getAllocatedQuantity();
            double remainingQuantity = requestItem.getQuantity() - allocatedQuantity;
            if (allocation.getQuantity() > remainingQuantity) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Allocation exceeds the remaining requested quantity");
            }

            stockMovementService.issue(new StockIssueRequest(
                    allocation.getItemId(),
                    branchId,
                    allocation.getQuantity(),
                    allocationRequest.getAllocatedByUserId(),
                    "Internal request #" + requestId + " allocation"
            ));

            requestItem.setAllocatedQuantity(allocatedQuantity + allocation.getQuantity());
            internalRequestItemRepository.save(requestItem);
        }
    }



}