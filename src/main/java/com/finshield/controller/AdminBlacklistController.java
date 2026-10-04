package com.finshield.controller;

import com.finshield.dto.request.BlacklistRequest;
import com.finshield.entity.BlacklistEntry;
import com.finshield.service.BlacklistService;
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
@RequestMapping("/api/admin/blacklist")
@RequiredArgsConstructor
@Tag(name = "Admin — Blacklist")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBlacklistController {

    private final BlacklistService blacklistService;

    @PostMapping
    @Operation(summary = "Add an entity (account/UPI/device ID) to the blacklist")
    public ResponseEntity<BlacklistEntry> add(@Valid @RequestBody BlacklistRequest request,
                                               Authentication authentication) {
        return ResponseEntity.ok(blacklistService.addEntry(request, authentication.getName()));
    }

    @GetMapping
    @Operation(summary = "List all blacklisted entities")
    public ResponseEntity<List<BlacklistEntry>> getAll() {
        return ResponseEntity.ok(blacklistService.getAll());
    }
}
