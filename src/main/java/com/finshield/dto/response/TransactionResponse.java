package com.finshield.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long transactionId;
    private String senderAccountId;
    private String receiverAccountId;
    private BigDecimal amount;
    private String status;       // SUCCESS | FLAGGED | BLOCKED
    private int riskScore;
    private List<String> triggeredRules;
    private LocalDateTime timestamp;
}
