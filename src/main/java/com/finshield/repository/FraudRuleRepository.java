package com.finshield.repository;

import com.finshield.entity.FraudRule;
import com.finshield.entity.RuleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FraudRuleRepository extends JpaRepository<FraudRule, Long> {

    Optional<FraudRule> findByRuleType(RuleType ruleType);

    List<FraudRule> findByActiveTrue();
}
