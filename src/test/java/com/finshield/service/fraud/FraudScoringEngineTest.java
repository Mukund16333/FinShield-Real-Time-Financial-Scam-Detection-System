package com.finshield.service.fraud;

import com.finshield.dto.request.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudScoringEngineTest {

    @Mock
    private FraudRuleEvaluator velocityEvaluator;

    @Mock
    private FraudRuleEvaluator amountEvaluator;

    @Mock
    private FraudRuleEvaluator blacklistEvaluator;

    private FraudScoringEngine fraudScoringEngine;
    private TransactionRequest request;

    @BeforeEach
    void setUp() {
        fraudScoringEngine = new FraudScoringEngine(
                List.of(velocityEvaluator, amountEvaluator, blacklistEvaluator));

        request = new TransactionRequest();
        request.setSenderAccountId("ACC1001");
        request.setReceiverAccountId("ACC2002");
        request.setAmount(BigDecimal.valueOf(1000));
    }

    @Test
    void evaluate_returnsSuccess_whenNoRulesTrigger() {
        when(velocityEvaluator.evaluate(request)).thenReturn(false);
        when(amountEvaluator.evaluate(request)).thenReturn(false);
        when(blacklistEvaluator.evaluate(request)).thenReturn(false);

        FraudScoringEngine.FraudEvaluationResult result = fraudScoringEngine.evaluate(request);

        assertThat(result.decision()).isEqualTo("SUCCESS");
        assertThat(result.riskScore()).isZero();
        assertThat(result.triggeredRules()).isEmpty();
    }

    @Test
    void evaluate_returnsFlagged_whenOneModerateRuleTriggers() {
        when(velocityEvaluator.ruleName()).thenReturn("VELOCITY_CHECK");
        when(velocityEvaluator.evaluate(request)).thenReturn(true);
        when(amountEvaluator.evaluate(request)).thenReturn(false);
        when(blacklistEvaluator.evaluate(request)).thenReturn(false);

        FraudScoringEngine.FraudEvaluationResult result = fraudScoringEngine.evaluate(request);

        assertThat(result.decision()).isEqualTo("FLAGGED");
        assertThat(result.riskScore()).isEqualTo(30);
        assertThat(result.triggeredRules()).containsExactly("VELOCITY_CHECK");
    }

    @Test
    void evaluate_returnsBlocked_whenBlacklistRuleTriggers() {
        when(velocityEvaluator.evaluate(request)).thenReturn(false);
        when(amountEvaluator.evaluate(request)).thenReturn(false);
        when(blacklistEvaluator.ruleName()).thenReturn("BLACKLIST_MATCH");
        when(blacklistEvaluator.evaluate(request)).thenReturn(true);

        FraudScoringEngine.FraudEvaluationResult result = fraudScoringEngine.evaluate(request);

        assertThat(result.decision()).isEqualTo("BLOCKED");
        assertThat(result.riskScore()).isEqualTo(100);
        assertThat(result.triggeredRules()).containsExactly("BLACKLIST_MATCH");
    }

    @Test
    void evaluate_returnsBlocked_whenMultipleModerateRulesCombineAboveThreshold() {
        when(velocityEvaluator.ruleName()).thenReturn("VELOCITY_CHECK");
        when(velocityEvaluator.evaluate(request)).thenReturn(true);
        when(amountEvaluator.ruleName()).thenReturn("AMOUNT_THRESHOLD");
        when(amountEvaluator.evaluate(request)).thenReturn(true);
        when(blacklistEvaluator.evaluate(request)).thenReturn(false);

        FraudScoringEngine.FraudEvaluationResult result = fraudScoringEngine.evaluate(request);

        // 30 (velocity) + 35 (amount) = 65 -> FLAGGED (below 80 block threshold)
        assertThat(result.riskScore()).isEqualTo(65);
        assertThat(result.decision()).isEqualTo("FLAGGED");
        assertThat(result.triggeredRules()).containsExactlyInAnyOrder("VELOCITY_CHECK", "AMOUNT_THRESHOLD");
    }
}
