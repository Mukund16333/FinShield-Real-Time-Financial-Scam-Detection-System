package com.finshield.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlacklistRequest {

    @NotBlank
    private String identifier; // account number / UPI ID / device ID

    @NotBlank
    private String reason;
}
