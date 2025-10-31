package com.sap.adds_service.common.infrastructure.output.dtos;

import java.util.UUID;

public record CompanyResponseDTO(
        UUID id,
        String name,
        String address,
        String phoneNumber
) {
}
