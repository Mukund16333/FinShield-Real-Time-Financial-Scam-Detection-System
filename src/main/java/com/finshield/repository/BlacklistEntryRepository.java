package com.finshield.repository;

import com.finshield.entity.BlacklistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BlacklistEntryRepository extends JpaRepository<BlacklistEntry, Long> {

    Optional<BlacklistEntry> findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);

    /** Single round-trip check for sender + receiver (+ device/UPI ids). */
    List<BlacklistEntry> findByIdentifierIn(Collection<String> identifiers);
}
