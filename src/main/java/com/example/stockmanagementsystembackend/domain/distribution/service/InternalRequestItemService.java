package com.example.stockmanagementsystembackend.domain.distribution.service;

import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItem;
import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItemId;
import com.example.stockmanagementsystembackend.domain.distribution.repository.InternalRequestItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternalRequestItemService {

    private final InternalRequestItemRepository internalRequestItemRepository;

    public InternalRequestItemService(InternalRequestItemRepository internalRequestItemRepository) {
        this.internalRequestItemRepository = internalRequestItemRepository;
    }

    public List<InternalRequestItem> getAllItems() {
        return internalRequestItemRepository.findAll();
    }

    public InternalRequestItem createItem(InternalRequestItem item) {
        return internalRequestItemRepository.save(item);
    }

    public InternalRequestItem updateItem(
            Integer requestId,
            Integer itemId,
            InternalRequestItem item) {

        InternalRequestItemId id = new InternalRequestItemId();
        id.setRequestId(requestId);
        id.setItemId(itemId);

        InternalRequestItem existingItem =
                internalRequestItemRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Request item not found"));

        existingItem.setItem(item.getItem());
        existingItem.setQuantity(item.getQuantity());

        return internalRequestItemRepository.save(existingItem);
    }

    public void deleteItem(Integer requestId, Integer itemId) {

        InternalRequestItemId id = new InternalRequestItemId();
        id.setRequestId(requestId);
        id.setItemId(itemId);

        internalRequestItemRepository.deleteById(id);
    }
}