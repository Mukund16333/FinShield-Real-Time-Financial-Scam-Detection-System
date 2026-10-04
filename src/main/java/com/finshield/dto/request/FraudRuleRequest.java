package com.finshield.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FraudRuleRequest {

    @NotBlank
    private String ruleType; // VELOCITY | AMOUNT | BLACKLIST

    @NotBlank
    private String config; // JSON-encoded rule configuration, e.g. {"maxCount":5,"windowMinutes":10}

    @NotNull
    private Boolean active;
}
