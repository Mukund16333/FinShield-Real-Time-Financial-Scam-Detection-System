package com.finshield.service.fraud;

import com.finshield.dto.request.TransactionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Flags a transaction whose amount exceeds a configured absolute threshold.
 * Threshold is externalized so it can be tuned without a redeploy.
 *
 * finshield:
 *   fraud:
 *     amount-threshold: 50000
 */
@Component
public class AmountThresholdEvaluator implements FraudRuleEvaluator {

    @Value("${finshield.fraud.amount-threshold:50000}")
    private BigDecimal amountThreshold;

    @Override
    public String ruleName() {
        return "AMOUNT_THRESHOLD";
    }

    @Override
    public boolean evaluate(TransactionRequest request) {
        return request.getAmount().compareTo(amountThreshold) >= 0;
    }
}
