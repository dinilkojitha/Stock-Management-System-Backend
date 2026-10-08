/**
 * JPA repository interface for querying InternalRequest records from the database.
 */
package com.example.stockmanagementsystembackend.domain.distribution.repository;

import com.example.stockmanagementsystembackend.domain.distribution.entity.Internalrequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InternalRequestRepository extends JpaRepository<Internalrequest, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Internalrequest r where r.id = :requestId")
    java.util.Optional<Internalrequest> findByIdForUpdate(@Param("requestId") Integer requestId);
}
