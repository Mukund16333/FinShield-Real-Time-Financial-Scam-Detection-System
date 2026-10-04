package com.finshield.repository;

import com.finshield.entity.Transaction;
import com.finshield.entity.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByReference(String reference);

    /** Velocity rule: every attempt counts, including flagged/blocked ones. */
    long countBySenderAccountIdAndTimestampAfter(String senderAccountId, Instant since);

    /** Amount-deviation rule: how much history does this sender have? */
    long countBySenderAccountIdAndStatus(String senderAccountId, TransactionStatus status);

    /** Historical average; null when the sender has no matching history. */
    @Query("select avg(t.amount) from Transaction t "
            + "where t.senderAccountId = :accountId and t.status = :status")
    Double averageAmountBySenderAndStatus(@Param("accountId") String accountId,
                                          @Param("status") TransactionStatus status);

    /** Customer view: transactions the account sent or received. */
    Page<Transaction> findBySenderAccountIdOrReceiverAccountId(String senderAccountId,
                                                               String receiverAccountId,
                                                               Pageable pageable);
}
