package com.sap.adds_service.adds.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record DurationView(
        UUID id,
        int days,
        LocalDateTime createdAt
) {
}
