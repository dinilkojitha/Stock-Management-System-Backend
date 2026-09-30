package com.example.stockmanagementsystembackend.domain.distribution.repository;

import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItem;
import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InternalRequestItemRepository
        extends JpaRepository<InternalRequestItem, InternalRequestItemId> {

    List<InternalRequestItem> findByIdRequestId(Integer requestId);

    List<InternalRequestItem> findAllByOrderByIdRequestIdAsc();
}