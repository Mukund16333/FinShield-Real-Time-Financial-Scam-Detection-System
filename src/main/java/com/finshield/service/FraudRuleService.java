package com.finshield.service;

import com.finshield.dto.request.FraudRuleRequest;
import com.finshield.entity.FraudRule;
import com.finshield.repository.FraudRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudRuleService {

    private final FraudRuleRepository fraudRuleRepository;
    private final AuditService auditService;

    public FraudRule createOrUpdate(FraudRuleRequest request, String adminEmail) {
        FraudRule rule = new FraudRule();
        rule.setRuleType(request.getRuleType());
        rule.setConfig(request.getConfig());
        rule.setActive(request.getActive());
        FraudRule saved = fraudRuleRepository.save(rule);

        auditService.log(adminEmail, "FRAUD_RULE_UPDATE",
                "Rule " + request.getRuleType() + " set to active=" + request.getActive()
                        + ", config=" + request.getConfig());

        return saved;
    }

    public List<FraudRule> getAll() {
        return fraudRuleRepository.findAll();
    }
}
