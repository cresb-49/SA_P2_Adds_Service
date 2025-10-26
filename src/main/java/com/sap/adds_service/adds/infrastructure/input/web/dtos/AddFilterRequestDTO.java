package com.sap.adds_service.adds.infrastructure.input.web.dtos;

import com.sap.adds_service.adds.domain.AddFilter;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;

import java.time.LocalDateTime;
import java.util.UUID;

public record AddFilterRequestDTO(
        AddType type,
        PaymentState paymentState,
        Boolean active,
        UUID cinemaId,
        UUID userId,
        LocalDateTime minPaymentDate,
        LocalDateTime maxPaymentDate
) {
    public AddFilter toDomain() {
        return new AddFilter(type, paymentState, active, cinemaId, userId, minPaymentDate, maxPaymentDate);
    }
}
