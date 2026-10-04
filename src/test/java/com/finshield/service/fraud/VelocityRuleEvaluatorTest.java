package com.finshield.service.fraud;

import com.finshield.dto.request.TransactionRequest;
import com.finshield.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VelocityRuleEvaluatorTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private VelocityRuleEvaluator velocityRuleEvaluator;

    private TransactionRequest request;

    @BeforeEach
    void setUp() {
        request = new TransactionRequest();
        request.setSenderAccountId("ACC1001");
        request.setReceiverAccountId("ACC2002");
        request.setAmount(BigDecimal.valueOf(1000));
    }

    @Test
    void ruleName_returnsVelocityCheck() {
        assertThat(velocityRuleEvaluator.ruleName()).isEqualTo("VELOCITY_CHECK");
    }

    @Test
    void evaluate_returnsTrue_whenTransactionCountAtOrAboveThreshold() {
        when(transactionRepository.countBySenderAccountIdAndTimestampAfter(eq("ACC1001"), any(LocalDateTime.class)))
                .thenReturn(5L);

        boolean result = velocityRuleEvaluator.evaluate(request);

        assertThat(result).isTrue();
    }

    @Test
    void evaluate_returnsFalse_whenTransactionCountBelowThreshold() {
        when(transactionRepository.countBySenderAccountIdAndTimestampAfter(eq("ACC1001"), any(LocalDateTime.class)))
                .thenReturn(2L);

        boolean result = velocityRuleEvaluator.evaluate(request);

        assertThat(result).isFalse();
    }
}
