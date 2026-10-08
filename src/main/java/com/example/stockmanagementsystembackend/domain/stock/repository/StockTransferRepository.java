package com.example.stockmanagementsystembackend.domain.stock.repository;

import com.example.stockmanagementsystembackend.domain.stock.entity.Branchtransferrequest;
import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockTransferRepository extends JpaRepository<Branchtransferrequest, Integer> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select t from Branchtransferrequest t where t.id = :transferId")
	Optional<Branchtransferrequest> findByIdForUpdate(@Param("transferId") Integer transferId);

	List<Branchtransferrequest> findByDestinationBranch(Branch branch);
	List<Branchtransferrequest> findBySourceBranch(Branch branch);
	long countByDestinationBranch(Branch branch);
	long countBySourceBranch(Branch branch);
	long countByDestinationBranchAndStatus_NameIgnoreCase(Branch branch, String statusName);
	long countBySourceBranchAndStatus_NameIgnoreCase(Branch branch, String statusName);
}
