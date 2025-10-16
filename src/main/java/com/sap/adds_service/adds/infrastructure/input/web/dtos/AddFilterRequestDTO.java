package com.sap.adds_service.adds.infrastructure.input.web.dtos;

import com.sap.adds_service.adds.application.usecases.findadd.dtos.AddFilter;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;

import java.util.UUID;

public record AddFilterRequestDTO(
        AddType type,
        PaymentState paymentState,
        Boolean active,
        UUID cinemaId,
        UUID userId
) {
    public AddFilter toDomain() {
        return new AddFilter(type, paymentState, active, cinemaId, userId);
    }
}
