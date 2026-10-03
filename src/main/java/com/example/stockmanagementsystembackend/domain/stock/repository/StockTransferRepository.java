package com.example.stockmanagementsystembackend.domain.stock.repository;

import com.example.stockmanagementsystembackend.domain.stock.entity.Branchtransferrequest;
import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockTransferRepository extends JpaRepository<Branchtransferrequest, Integer> {
	List<Branchtransferrequest> findByDestinationBranch(Branch branch);
	List<Branchtransferrequest> findBySourceBranch(Branch branch);
	long countByDestinationBranch(Branch branch);
	long countBySourceBranch(Branch branch);
	long countByDestinationBranchAndStatus_NameIgnoreCase(Branch branch, String statusName);
	long countBySourceBranchAndStatus_NameIgnoreCase(Branch branch, String statusName);
}
