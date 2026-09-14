package com.example.stockmanagementsystembackend.domain.distribution.service;

import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItem;
import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItemId;
import com.example.stockmanagementsystembackend.domain.distribution.repository.InternalRequestItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class InternalRequestItemService {

    private final InternalRequestItemRepository internalRequestItemRepository;

    public InternalRequestItemService(
            InternalRequestItemRepository internalRequestItemRepository) {
        this.internalRequestItemRepository = internalRequestItemRepository;
    }

    public List<InternalRequestItem> getAllRequestItems() {
        return internalRequestItemRepository.findAll();
    }

    public InternalRequestItem createRequestItem(
            InternalRequestItem requestItem) {
        return internalRequestItemRepository.save(requestItem);
    }

    public InternalRequestItem updateRequestItem(
            InternalRequestItemId id,
            InternalRequestItem requestItem) {

        InternalRequestItem existingItem =
                internalRequestItemRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Request item not found"));

        existingItem.setInternalrequestOrdertid(
                requestItem.getInternalrequestOrdertid());

        existingItem.setInventoryitemItem(
                requestItem.getInventoryitemItem());

        existingItem.setQuantity(
                requestItem.getQuantity());

        return internalRequestItemRepository.save(existingItem);
    }

    public void deleteRequestItem(InternalRequestItemId id) {
        internalRequestItemRepository.deleteById(id);
    }
}