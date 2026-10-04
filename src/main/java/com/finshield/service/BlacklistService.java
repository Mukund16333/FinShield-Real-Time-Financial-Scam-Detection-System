package com.finshield.service;

import com.finshield.dto.request.BlacklistRequest;
import com.finshield.entity.BlacklistEntry;
import com.finshield.repository.BlacklistEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistEntryRepository blacklistEntryRepository;
    private final AuditService auditService;

    public BlacklistEntry addEntry(BlacklistRequest request, String adminEmail) {
        BlacklistEntry entry = new BlacklistEntry();
        entry.setIdentifier(request.getIdentifier());
        entry.setReason(request.getReason());
        entry.setAddedBy(adminEmail);
        BlacklistEntry saved = blacklistEntryRepository.save(entry);

        auditService.log(adminEmail, "BLACKLIST_ADD",
                "Added identifier " + request.getIdentifier() + " — reason: " + request.getReason());

        return saved;
    }

    public List<BlacklistEntry> getAll() {
        return blacklistEntryRepository.findAll();
    }
}
