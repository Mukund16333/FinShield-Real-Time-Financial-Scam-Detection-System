package com.finshield.service.fraud;

import com.finshield.dto.request.TransactionRequest;
import com.finshield.util.RiskScoreCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates all registered FraudRuleEvaluator beans against an incoming
 * transaction, producing a composite risk score and a final decision.
 *
 * Spring autowires every FraudRuleEvaluator implementation into the list,
 * so adding a new rule (e.g. GeoVelocityEvaluator) requires zero changes here.
 */
@Component
@RequiredArgsConstructor
public class FraudScoringEngine {

    private final List<FraudRuleEvaluator> ruleEvaluators;

    public FraudEvaluationResult evaluate(TransactionRequest request) {
        List<String> triggeredRules = new ArrayList<>();

        for (FraudRuleEvaluator evaluator : ruleEvaluators) {
            if (evaluator.evaluate(request)) {
                triggeredRules.add(evaluator.ruleName());
            }
        }

        int riskScore = RiskScoreCalculator.calculateScore(triggeredRules);
        String decision = RiskScoreCalculator.decide(riskScore);

        return new FraudEvaluationResult(decision, riskScore, triggeredRules);
    }

    public record FraudEvaluationResult(String decision, int riskScore, List<String> triggeredRules) {}
}
