package com.sap.adds_service.adds.domain.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PriceView(
        UUID id,
        UUID cinemaId,
        BigDecimal amountTextBanner,
        BigDecimal amountMediaVertical,
        BigDecimal amountMediaHorizontal,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}