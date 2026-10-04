package com.finshield.service.fraud;

import com.finshield.dto.request.TransactionRequest;

/**
 * Strategy interface for a single fraud rule. Add a new rule by implementing
 * this interface and registering the bean — FraudScoringEngine autowires all
 * implementations, so no existing code needs to change.
 */
public interface FraudRuleEvaluator {

    /**
     * @return the rule identifier this evaluator produces when triggered,
     *         e.g. "VELOCITY_CHECK", "AMOUNT_THRESHOLD", "BLACKLIST_MATCH"
     */
    String ruleName();

    /**
     * @return true if this transaction trips the rule
     */
    boolean evaluate(TransactionRequest request);
}
