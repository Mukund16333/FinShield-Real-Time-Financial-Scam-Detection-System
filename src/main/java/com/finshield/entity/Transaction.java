package com.finshield.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transactions", indexes = {
        // Velocity and historical-average lookups filter on sender + time
        @Index(name = "idx_txn_sender_ts", columnList = "sender_account_id, timestamp"),
        @Index(name = "idx_txn_receiver", columnList = "receiver_account_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Public identifier returned to clients, e.g. TXN98213. */
    @Column(nullable = false, unique = true, updatable = false, length = 30)
    private String reference;

    @Column(name = "sender_account_id", nullable = false, length = 40)
    private String senderAccountId;

    @Column(name = "receiver_account_id", nullable = false, length = 40)
    private String receiverAccountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    /** Composite risk score, 0-100. */
    @Column(name = "risk_score", nullable = false)
    private int riskScore;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "transaction_triggered_rules",
            joinColumns = @JoinColumn(name = "transaction_id"))
    @Column(name = "rule_name", length = 60)
    @Builder.Default
    private List<String> triggeredRules = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
