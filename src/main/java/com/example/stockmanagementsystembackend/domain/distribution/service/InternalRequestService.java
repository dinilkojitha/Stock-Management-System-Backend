package com.example.stockmanagementsystembackend.domain.distribution.service;

import com.example.stockmanagementsystembackend.domain.distribution.dto.CreateInternalRequestDTO;
import com.example.stockmanagementsystembackend.domain.distribution.dto.RequestItemDTO;
import com.example.stockmanagementsystembackend.domain.distribution.entity.Internalrequest;
import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItem;
import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItemId;
import com.example.stockmanagementsystembackend.domain.distribution.repository.InternalRequestItemRepository;
import com.example.stockmanagementsystembackend.domain.distribution.repository.InternalRequestRepository;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.organization.entity.Department;
import com.example.stockmanagementsystembackend.user.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class InternalRequestService {

    private final InternalRequestRepository internalRequestRepository;
    private final InternalRequestItemRepository internalRequestItemRepository;
    private final EntityManager entityManager;


    public InternalRequestService(
            InternalRequestRepository internalRequestRepository,
            InternalRequestItemRepository internalRequestItemRepository,
            EntityManager entityManager) {

        this.internalRequestRepository = internalRequestRepository;
        this.internalRequestItemRepository = internalRequestItemRepository;
        this.entityManager = entityManager;
    }

    public List<Internalrequest> getAllRequest() {
        return internalRequestRepository.findAll();
    }

    public Internalrequest createRequest(Internalrequest request) {
        return internalRequestRepository.save(request);
    }

    public Internalrequest updateRequest(Integer id, Internalrequest request) {

        Internalrequest existingRequest =
                internalRequestRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Request not found"));

        existingRequest.setStatus(request.getStatus());

        return internalRequestRepository.save(existingRequest);
    }

    public void deleteRequest(Integer id) {
        internalRequestRepository.deleteById(id);
    }

    @Transactional
    public Internalrequest createCompleteRequest(
            CreateInternalRequestDTO dto) {

        Internalrequest request = new Internalrequest();

        Department department = entityManager.getReference(
                Department.class,
                dto.getDepartmentId()
        );

        User user=entityManager.getReference(
                User.class,
                dto.getRequestedByUserId()
        );

        request.setDepartment(department);
        request.setRequestedByUser(user);

        request.setRequestedAt(dto.getRequestedAt());
        request.setStatus(dto.getStatus());

        Internalrequest savedRequest=internalRequestRepository.save(request);

        for(RequestItemDTO itemDTO : dto.getItems()){

            InternalRequestItem requestItem=new InternalRequestItem();

            InternalRequestItemId itemId=new InternalRequestItemId();

            itemId.setRequestId(
                    savedRequest.getId()
            );

            itemId.setItemId(
                    itemDTO.getItemId()
            );

            requestItem.setId(itemId);

            InventoryItem inventoryItem=
                    entityManager.getReference(
                            InventoryItem.class,
                            itemDTO.getItemId()
                    );

            requestItem.setItem(inventoryItem);

            requestItem.setQuantity(
                    itemDTO.getQuantity()
            );

            requestItem.setAllocatedQuantity(0.0);

            internalRequestItemRepository.save(requestItem);


        }


        return savedRequest;


    }


}