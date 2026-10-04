package com.finshield.service.fraud;

import com.finshield.dto.request.TransactionRequest;
import com.finshield.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Flags an account that has exceeded a configured number of transactions
 * within a rolling time window.
 *
 * Defaults (move to FraudRule table-driven config if per-deployment tuning
 * is needed): more than 5 transactions from the same sender in 10 minutes.
 */
@Component
@RequiredArgsConstructor
public class VelocityRuleEvaluator implements FraudRuleEvaluator {

    private static final int MAX_TRANSACTIONS = 5;
    private static final int WINDOW_MINUTES = 10;

    private final TransactionRepository transactionRepository;

    @Override
    public String ruleName() {
        return "VELOCITY_CHECK";
    }

    @Override
    public boolean evaluate(TransactionRequest request) {
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(WINDOW_MINUTES);
        long recentCount = transactionRepository
                .countBySenderAccountIdAndTimestampAfter(request.getSenderAccountId(), windowStart);
        return recentCount >= MAX_TRANSACTIONS;
    }
}
