package com.finshield.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

/**
 * Append-only audit record. No setters, every column is non-updatable, Hibernate treats
 * the entity as @Immutable, and deletes are rejected in {@link #preventRemove()}.
 * The repository additionally exposes no update/delete methods.
 */
@Entity
@Immutable
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_actor", columnList = "actor_id"),
        @Index(name = "idx_audit_ts", columnList = "timestamp")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Acting user's id; null for system-initiated events. */
    @Column(name = "actor_id", updatable = false)
    private Long actorId;

    @Column(nullable = false, updatable = false, length = 60)
    private String action;

    /** Free-form description. Callers must mask account numbers and never include secrets. */
    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @PrePersist
    void onCreate() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    @PreRemove
    void preventRemove() {
        throw new IllegalStateException("Audit log entries are immutable and cannot be deleted");
    }
}
