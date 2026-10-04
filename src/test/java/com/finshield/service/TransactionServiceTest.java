package com.finshield.service;

import com.finshield.dto.request.TransactionRequest;
import com.finshield.dto.response.TransactionResponse;
import com.finshield.entity.Transaction;
import com.finshield.entity.TransactionStatus;
import com.finshield.exception.ResourceNotFoundException;
import com.finshield.repository.TransactionRepository;
import com.finshield.service.fraud.FraudScoringEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FraudScoringEngine fraudScoringEngine;

    @Mock
    private AlertService alertService;

    @Mock
    private AuditService auditService;

    private TransactionService transactionService;

    private TransactionRequest request;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(
                transactionRepository, fraudScoringEngine, alertService, auditService);

        request = new TransactionRequest();
        request.setSenderAccountId("ACC1001");
        request.setReceiverAccountId("ACC2002");
        request.setAmount(BigDecimal.valueOf(1000));
    }

    @Test
    void processTransaction_persistsCleanTransaction_andDoesNotAlert() {
        when(fraudScoringEngine.evaluate(request))
                .thenReturn(new FraudScoringEngine.FraudEvaluationResult("SUCCESS", 0, List.of()));

        Transaction saved = new Transaction();
        saved.setId(1L);
        saved.setSenderAccountId("ACC1001");
        saved.setReceiverAccountId("ACC2002");
        saved.setAmount(BigDecimal.valueOf(1000));
        saved.setStatus(TransactionStatus.SUCCESS);
        saved.setRiskScore(0);
        saved.setTimestamp(LocalDateTime.now());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponse response = transactionService.processTransaction(request);

        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getRiskScore()).isZero();
        verify(alertService, never()).createAlert(any(), any(), any());
        verify(auditService).log(eq("ACC1001"), eq("TRANSACTION_EVALUATED"), anyString());
    }

    @Test
    void processTransaction_flaggedTransaction_createsAlert() {
        when(fraudScoringEngine.evaluate(request))
                .thenReturn(new FraudScoringEngine.FraudEvaluationResult(
                        "FLAGGED", 45, List.of("AMOUNT_THRESHOLD")));

        Transaction saved = new Transaction();
        saved.setId(2L);
        saved.setSenderAccountId("ACC1001");
        saved.setReceiverAccountId("ACC2002");
        saved.setAmount(BigDecimal.valueOf(1000));
        saved.setStatus(TransactionStatus.FLAGGED);
        saved.setRiskScore(45);
        saved.setTimestamp(LocalDateTime.now());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponse response = transactionService.processTransaction(request);

        assertThat(response.getStatus()).isEqualTo("FLAGGED");
        ArgumentCaptor<String> severityCaptor = ArgumentCaptor.forClass(String.class);
        verify(alertService).createAlert(eq(saved), severityCaptor.capture(), anyString());
        assertThat(severityCaptor.getValue()).isEqualTo("MEDIUM");
    }

    @Test
    void processTransaction_blockedTransaction_createsHighSeverityAlert() {
        when(fraudScoringEngine.evaluate(request))
                .thenReturn(new FraudScoringEngine.FraudEvaluationResult(
                        "BLOCKED", 100, List.of("BLACKLIST_MATCH")));

        Transaction saved = new Transaction();
        saved.setId(3L);
        saved.setSenderAccountId("ACC1001");
        saved.setReceiverAccountId("ACC2002");
        saved.setAmount(BigDecimal.valueOf(1000));
        saved.setStatus(TransactionStatus.BLOCKED);
        saved.setRiskScore(100);
        saved.setTimestamp(LocalDateTime.now());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponse response = transactionService.processTransaction(request);

        assertThat(response.getStatus()).isEqualTo("BLOCKED");
        verify(alertService).createAlert(eq(saved), eq("HIGH"), anyString());
    }

    @Test
    void getById_returnsTransaction_whenFound() {
        Transaction transaction = new Transaction();
        transaction.setId(5L);
        transaction.setSenderAccountId("ACC1001");
        transaction.setReceiverAccountId("ACC2002");
        transaction.setAmount(BigDecimal.valueOf(2500));
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setRiskScore(0);
        transaction.setTimestamp(LocalDateTime.now());

        when(transactionRepository.findById(5L)).thenReturn(Optional.of(transaction));

        TransactionResponse response = transactionService.getById(5L);

        assertThat(response.getTransactionId()).isEqualTo(5L);
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
    }

    @Test
    void getById_throwsResourceNotFound_whenMissing() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
