package com.example.stockmanagementsystembackend.domain.distribution.service;

import com.example.stockmanagementsystembackend.domain.distribution.repository.InternalRequestRepository;
import com.example.stockmanagementsystembackend.domain.distribution.repository.InternalRequestItemRepository;
import com.example.stockmanagementsystembackend.domain.stock.repository.StockItemRepository;
import com.example.stockmanagementsystembackend.domain.stock.repository.StockRepository;
import com.example.stockmanagementsystembackend.domain.distribution.dto.StockAllocationRequestDTO;
import com.example.stockmanagementsystembackend.domain.distribution.entity.Internalrequest;
import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItem;
import com.example.stockmanagementsystembackend.domain.stock.entity.StockItem;
import com.example.stockmanagementsystembackend.domain.stock.entity.Stock;
import com.example.stockmanagementsystembackend.domain.distribution.dto.StockAllocationDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockAllocationService {

    private final InternalRequestRepository internalRequestRepository;
    private final InternalRequestItemRepository internalRequestItemRepository;

    private final StockItemRepository stockItemRepository;

    private final StockRepository stockRepository;

    public StockAllocationService(
            InternalRequestRepository internalRequestRepository,
            InternalRequestItemRepository internalRequestItemRepository,
            StockItemRepository stockItemRepository,
            StockRepository stockRepository) {

        this.internalRequestRepository = internalRequestRepository;
        this.internalRequestItemRepository = internalRequestItemRepository;
        this.stockItemRepository = stockItemRepository;
        this.stockRepository = stockRepository;
    }
    @Transactional
    public void allocateStock(
            Integer requestId,
            StockAllocationRequestDTO allocationRequest) {

        Internalrequest request = internalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        List<InternalRequestItem> requestItems =
                internalRequestItemRepository.findByIdRequestId(requestId);
        for (InternalRequestItem requestItem : requestItems) {

            Integer itemId = requestItem.getId().getItemId();

            StockAllocationDTO allocation = allocationRequest.getItems()
                    .stream()
                    .filter(item -> item.getItemId().equals(itemId))
                    .findFirst()
                    .orElse(null);

            if (allocation == null) {
                continue;
            }


            Double allocationQuantity = allocation.getQuantity();
            Double requestedQuantity = requestItem.getQuantity();
            Double allocatedQuantity = requestItem.getAllocatedQuantity();
            Double remainingQuantity = requestedQuantity - allocatedQuantity;

            List<StockItem> stockItems =
                    stockItemRepository.findByItem_Id(itemId);

            for (StockItem stockItem : stockItems) {

                if (allocationQuantity <= 0 || remainingQuantity <= 0) {
                    break;
                }

                Stock stock = stockItem.getStock();
                Double availableStock = stock.getQuantity();

                Double amountToAllocate = Math.min(
                        allocationQuantity,
                        Math.min(remainingQuantity, availableStock)
                );

                stock.setQuantity(availableStock - amountToAllocate);

                stockRepository.save(stock);

                allocationQuantity -= amountToAllocate;
                remainingQuantity -= amountToAllocate;

            }

            Double newlyAllocated = allocation.getQuantity() - allocationQuantity;

            requestItem.setAllocatedQuantity(
                    allocatedQuantity + newlyAllocated
            );


            internalRequestItemRepository.save(requestItem);
        }
    }



}