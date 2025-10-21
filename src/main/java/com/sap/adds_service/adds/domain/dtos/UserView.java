package com.sap.adds_service.adds.domain.dtos;

import java.util.UUID;

public record UserView(
        UUID id,
        String firstName,
        String lastName,
        String email
) {
}
