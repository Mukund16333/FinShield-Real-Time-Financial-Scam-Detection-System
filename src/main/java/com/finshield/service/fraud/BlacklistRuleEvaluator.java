package com.finshield.service.fraud;

import com.finshield.dto.request.TransactionRequest;
import com.finshield.repository.BlacklistEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Flags a transaction if either the sender or receiver identifier appears
 * in the BlacklistEntry table.
 */
@Component
@RequiredArgsConstructor
public class BlacklistRuleEvaluator implements FraudRuleEvaluator {

    private final BlacklistEntryRepository blacklistEntryRepository;

    @Override
    public String ruleName() {
        return "BLACKLIST_MATCH";
    }

    @Override
    public boolean evaluate(TransactionRequest request) {
        return blacklistEntryRepository.existsByIdentifier(request.getSenderAccountId())
                || blacklistEntryRepository.existsByIdentifier(request.getReceiverAccountId());
    }
}
