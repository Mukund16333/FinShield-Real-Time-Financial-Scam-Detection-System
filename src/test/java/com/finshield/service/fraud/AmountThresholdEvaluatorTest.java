package com.finshield.service.fraud;

import com.finshield.dto.request.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AmountThresholdEvaluatorTest {

    private AmountThresholdEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new AmountThresholdEvaluator();
        // @Value fields aren't populated outside a Spring context, so inject the threshold directly.
        ReflectionTestUtils.setField(evaluator, "amountThreshold", BigDecimal.valueOf(50000));
    }

    @Test
    void ruleName_returnsAmountThreshold() {
        assertThat(evaluator.ruleName()).isEqualTo("AMOUNT_THRESHOLD");
    }

    @Test
    void evaluate_returnsTrue_whenAmountExceedsThreshold() {
        TransactionRequest request = new TransactionRequest();
        request.setSenderAccountId("ACC1001");
        request.setReceiverAccountId("ACC2002");
        request.setAmount(BigDecimal.valueOf(75000));

        assertThat(evaluator.evaluate(request)).isTrue();
    }

    @Test
    void evaluate_returnsFalse_whenAmountBelowThreshold() {
        TransactionRequest request = new TransactionRequest();
        request.setSenderAccountId("ACC1001");
        request.setReceiverAccountId("ACC2002");
        request.setAmount(BigDecimal.valueOf(1500));

        assertThat(evaluator.evaluate(request)).isFalse();
    }

    @Test
    void evaluate_returnsTrue_whenAmountEqualsThreshold() {
        TransactionRequest request = new TransactionRequest();
        request.setSenderAccountId("ACC1001");
        request.setReceiverAccountId("ACC2002");
        request.setAmount(BigDecimal.valueOf(50000));

        assertThat(evaluator.evaluate(request)).isTrue();
    }
}
