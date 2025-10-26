package com.sap.adds_service.adds.domain;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record AddFilter(
        AddType type,
        PaymentState paymentState,
        Boolean active,
        UUID cinemaId,
        UUID userId,
        LocalDateTime minPaymentDate,
        LocalDateTime maxPaymentDate
) {
}
