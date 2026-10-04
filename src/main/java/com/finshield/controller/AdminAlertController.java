package com.finshield.controller;

import com.finshield.dto.response.AlertResponse;
import com.finshield.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/alerts")
@RequiredArgsConstructor
@Tag(name = "Admin — Alerts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAlertController {

    private final AlertService alertService;

    @GetMapping
    @Operation(summary = "List all flagged/blocked transaction alerts")
    public ResponseEntity<List<AlertResponse>> getAll() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }
}
