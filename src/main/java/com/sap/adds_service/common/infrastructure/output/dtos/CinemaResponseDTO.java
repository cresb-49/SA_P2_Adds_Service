package com.sap.adds_service.common.infrastructure.output.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CinemaResponseDTO(
        UUID id,
        CompanyResponseDTO company,
        String name,
        BigDecimal costPerDay,
        LocalDate createdAt
) {
}