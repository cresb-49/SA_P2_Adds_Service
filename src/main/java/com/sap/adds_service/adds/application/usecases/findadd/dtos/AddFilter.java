package com.sap.adds_service.adds.application.usecases.findadd.dtos;

import com.sap.adds_service.adds.domain.AddType;

import java.util.UUID;

public record AddFilter(
        AddType type,
        Boolean active,
        UUID cinemaId,
        UUID userId
) {
}
