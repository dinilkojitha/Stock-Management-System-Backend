package com.example.stockmanagementsystembackend.domain.stock.repository;

import com.example.stockmanagementsystembackend.domain.stock.entity.StockTransferRequest;
import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransferRequest, Integer> {
	List<StockTransferRequest> findByBranchBranchid(Branch branch);
	List<StockTransferRequest> findByToBranchBranchid(Branch branch);
}
