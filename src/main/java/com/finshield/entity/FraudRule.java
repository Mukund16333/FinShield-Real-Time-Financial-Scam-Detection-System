package com.finshield.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * One row per rule type. {@code config} holds rule-specific settings as JSON
 * (e.g. {"maxTransactions":5,"windowMinutes":10,"weight":35}), so new settings
 * never require a schema change.
 */
@Entity
@Table(name = "fraud_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false, unique = true, length = 30)
    private RuleType ruleType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String config;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}
