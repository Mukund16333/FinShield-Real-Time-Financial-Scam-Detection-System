package com.finshield.service;

import com.finshield.dto.request.TransactionRequest;
import com.finshield.dto.response.TransactionResponse;
import com.finshield.entity.Transaction;
import com.finshield.entity.TransactionStatus;
import com.finshield.exception.ResourceNotFoundException;
import com.finshield.repository.TransactionRepository;
import com.finshield.service.fraud.FraudScoringEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Central transaction-processing service: persists the incoming transaction,
 * runs it through the FraudScoringEngine, updates its status/riskScore
 * accordingly, and triggers an alert + audit entry when it is not clean.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final FraudScoringEngine fraudScoringEngine;
    private final AlertService alertService;
    private final AuditService auditService;

    public TransactionResponse processTransaction(TransactionRequest request) {
        FraudScoringEngine.FraudEvaluationResult result = fraudScoringEngine.evaluate(request);

        Transaction transaction = new Transaction();
        transaction.setSenderAccountId(request.getSenderAccountId());
        transaction.setReceiverAccountId(request.getReceiverAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.valueOf(result.decision()));
        transaction.setRiskScore(result.riskScore());

        Transaction saved = transactionRepository.save(transaction);

        auditService.log(request.getSenderAccountId(), "TRANSACTION_EVALUATED",
                "Transaction " + saved.getId() + " scored " + result.riskScore()
                        + " -> " + result.decision() + " (rules: " + result.triggeredRules() + ")");

        if (!result.decision().equals("SUCCESS")) {
            alertService.createAlert(saved, result.decision().equals("BLOCKED") ? "HIGH" : "MEDIUM",
                    "Transaction " + saved.getId() + " " + result.decision().toLowerCase()
                            + " — triggered rules: " + result.triggeredRules());
        }

        return TransactionResponse.builder()
                .transactionId(saved.getId())
                .senderAccountId(saved.getSenderAccountId())
                .receiverAccountId(saved.getReceiverAccountId())
                .amount(saved.getAmount())
                .status(saved.getStatus().name())
                .riskScore(saved.getRiskScore())
                .triggeredRules(result.triggeredRules())
                .timestamp(saved.getTimestamp())
                .build();
    }

    public TransactionResponse getById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));

        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .senderAccountId(transaction.getSenderAccountId())
                .receiverAccountId(transaction.getReceiverAccountId())
                .amount(transaction.getAmount())
                .status(transaction.getStatus().name())
                .riskScore(transaction.getRiskScore())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
