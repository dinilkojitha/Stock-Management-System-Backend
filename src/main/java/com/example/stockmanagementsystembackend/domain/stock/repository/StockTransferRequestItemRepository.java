package com.example.stockmanagementsystembackend.domain.stock.repository;

import com.example.stockmanagementsystembackend.domain.stock.entity.StockTransferRequestItem;
import com.example.stockmanagementsystembackend.domain.stock.entity.StockTransferRequestItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockTransferRequestItemRepository
        extends JpaRepository<StockTransferRequestItem, StockTransferRequestItemId> {
    List<StockTransferRequestItem> findByBranchtransferrequeastOrderidId(Integer transferId);
}