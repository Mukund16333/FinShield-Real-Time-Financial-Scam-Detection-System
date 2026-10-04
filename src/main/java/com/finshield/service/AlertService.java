package com.finshield.service;

import com.finshield.dto.response.AlertResponse;
import com.finshield.entity.Alert;
import com.finshield.entity.Transaction;
import com.finshield.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;

    public void createAlert(Transaction transaction, String severity, String message) {
        Alert alert = new Alert();
        alert.setTransactionId(transaction.getId());
        alert.setSeverity(severity);
        alert.setMessage(message);
        alert.setCreatedAt(LocalDateTime.now());
        alertRepository.save(alert);
    }

    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private AlertResponse toResponse(Alert alert) {
        return AlertResponse.builder()
                .alertId(alert.getId())
                .transactionId(alert.getTransactionId())
                .severity(alert.getSeverity())
                .message(alert.getMessage())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
