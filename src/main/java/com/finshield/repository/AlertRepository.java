package com.finshield.repository;

import com.finshield.entity.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    /** JOIN FETCH avoids lazy-loading the transaction per row (open-in-view is off). */
    @Query(value = "select a from Alert a join fetch a.transaction order by a.createdAt desc",
            countQuery = "select count(a) from Alert a")
    Page<Alert> findAllWithTransaction(Pageable pageable);
}
