package com.example.stockmanagementsystembackend.domain.stock.repository;

import com.example.stockmanagementsystembackend.domain.stock.entity.BranchTransferItem;
import com.example.stockmanagementsystembackend.domain.stock.entity.BranchTransferItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockTransferRequestItemRepository
        extends JpaRepository<BranchTransferItem, BranchTransferItemId> {
    List<BranchTransferItem> findByIdTransferId(Integer transferId);
}