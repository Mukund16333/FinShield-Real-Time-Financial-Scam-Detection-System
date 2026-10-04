package com.finshield.repository;

import com.finshield.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

/**
 * Deliberately NOT a JpaRepository: only append and read operations are exposed,
 * so no code path can update or delete audit records through this bean.
 */
public interface AuditLogRepository extends Repository<AuditLog, Long> {

    AuditLog save(AuditLog log);

    Page<AuditLog> findAll(Pageable pageable);

    Page<AuditLog> findByActorId(Long actorId, Pageable pageable);

    Page<AuditLog> findByAction(String action, Pageable pageable);
}
