package com.finshield.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "blacklist_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Account number, UPI ID, or device fingerprint. */
    @Column(nullable = false, unique = true, length = 100)
    private String identifier;

    @Column(nullable = false, length = 255)
    private String reason;

    /** User id of the admin who added the entry. Plain column (not FK) so history survives user deletion. */
    @Column(name = "added_by", nullable = false)
    private Long addedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
