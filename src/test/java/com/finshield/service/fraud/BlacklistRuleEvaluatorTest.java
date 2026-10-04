package com.finshield.service.fraud;

import com.finshield.dto.request.TransactionRequest;
import com.finshield.repository.BlacklistEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlacklistRuleEvaluatorTest {

    @Mock
    private BlacklistEntryRepository blacklistEntryRepository;

    @InjectMocks
    private BlacklistRuleEvaluator blacklistRuleEvaluator;

    private TransactionRequest request;

    @BeforeEach
    void setUp() {
        request = new TransactionRequest();
        request.setSenderAccountId("ACC1001");
        request.setReceiverAccountId("ACC9999");
        request.setAmount(BigDecimal.valueOf(1000));
    }

    @Test
    void ruleName_returnsBlacklistMatch() {
        assertThat(blacklistRuleEvaluator.ruleName()).isEqualTo("BLACKLIST_MATCH");
    }

    @Test
    void evaluate_returnsTrue_whenReceiverIsBlacklisted() {
        when(blacklistEntryRepository.existsByIdentifier("ACC1001")).thenReturn(false);
        when(blacklistEntryRepository.existsByIdentifier("ACC9999")).thenReturn(true);

        assertThat(blacklistRuleEvaluator.evaluate(request)).isTrue();
    }

    @Test
    void evaluate_returnsTrue_whenSenderIsBlacklisted() {
        when(blacklistEntryRepository.existsByIdentifier("ACC1001")).thenReturn(true);

        assertThat(blacklistRuleEvaluator.evaluate(request)).isTrue();
    }

    @Test
    void evaluate_returnsFalse_whenNeitherPartyIsBlacklisted() {
        when(blacklistEntryRepository.existsByIdentifier("ACC1001")).thenReturn(false);
        when(blacklistEntryRepository.existsByIdentifier("ACC9999")).thenReturn(false);

        assertThat(blacklistRuleEvaluator.evaluate(request)).isFalse();
    }
}
