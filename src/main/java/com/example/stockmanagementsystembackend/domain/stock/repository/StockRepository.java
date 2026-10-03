package com.example.stockmanagementsystembackend.domain.stock.repository;

import com.example.stockmanagementsystembackend.domain.stock.entity.Stock;
import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
	@Override
	@EntityGraph(attributePaths = "items")
	List<Stock> findAll();

	@Override
	@EntityGraph(attributePaths = "items")
	Optional<Stock> findById(Integer id);

	@EntityGraph(attributePaths = "items")
	List<Stock> findByBranch(Branch branch);

	@EntityGraph(attributePaths = "items")
	List<Stock> findByBranch_IdOrderByStockIdAsc(Integer branchId);

	@EntityGraph(attributePaths = "items")
	List<Stock> findDistinctByItems_IdOrderByStockIdAsc(Integer itemId);

	@EntityGraph(attributePaths = "items")
	List<Stock> findByExpiryDateBetweenOrderByExpiryDateAscStockIdAsc(LocalDate start, LocalDate end);

	@EntityGraph(attributePaths = "items")
	List<Stock> findByExpiryDateBeforeOrderByExpiryDateAscStockIdAsc(LocalDate date);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select s from Stock s where s.stockId = :stockId")
	Optional<Stock> findByIdForUpdate(@Param("stockId") Integer stockId);
}
