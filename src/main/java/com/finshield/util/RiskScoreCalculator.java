package com.finshield.util;

import java.util.List;
import java.util.Map;

/**
 * Combines individual fraud rule outcomes into a single composite risk
 * score (0-100) and maps it to a final decision.
 *
 * Each rule contributes a weighted point value when triggered. Weights are
 * intentionally simple/configurable here; move to FraudRule.config-driven
 * weights if per-rule tuning is required later.
 */
public class RiskScoreCalculator {

    private static final Map<String, Integer> RULE_WEIGHTS = Map.of(
            "VELOCITY_CHECK", 30,
            "AMOUNT_THRESHOLD", 35,
            "BLACKLIST_MATCH", 100 // an outright blacklist match should dominate the score
    );

    public static final int FLAG_THRESHOLD = 40;
    public static final int BLOCK_THRESHOLD = 80;

    public static int calculateScore(List<String> triggeredRules) {
        return triggeredRules.stream()
                .mapToInt(rule -> RULE_WEIGHTS.getOrDefault(rule, 0))
                .sum();
    }

    public static String decide(int score) {
        if (score >= BLOCK_THRESHOLD) return "BLOCKED";
        if (score >= FLAG_THRESHOLD) return "FLAGGED";
        return "SUCCESS";
    }
}
