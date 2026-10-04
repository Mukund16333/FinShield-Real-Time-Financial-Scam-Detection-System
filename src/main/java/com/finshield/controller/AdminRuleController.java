package com.finshield.controller;

import com.finshield.dto.request.FraudRuleRequest;
import com.finshield.entity.FraudRule;
import com.finshield.service.FraudRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rules")
@RequiredArgsConstructor
@Tag(name = "Admin — Fraud Rules")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRuleController {

    private final FraudRuleService fraudRuleService;

    @PostMapping
    @Operation(summary = "Create or update a fraud detection rule")
    public ResponseEntity<FraudRule> createOrUpdate(@Valid @RequestBody FraudRuleRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(fraudRuleService.createOrUpdate(request, authentication.getName()));
    }

    @GetMapping
    @Operation(summary = "List all fraud detection rules")
    public ResponseEntity<List<FraudRule>> getAll() {
        return ResponseEntity.ok(fraudRuleService.getAll());
    }
}
