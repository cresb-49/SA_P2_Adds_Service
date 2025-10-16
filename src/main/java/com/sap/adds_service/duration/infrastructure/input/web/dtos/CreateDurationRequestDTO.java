package com.sap.adds_service.duration.infrastructure.input.web.dtos;

import com.sap.adds_service.duration.application.usecases.createduration.dtos.CreateDurationDTO;

public record CreateDurationRequestDTO(
        int days
) {
    public CreateDurationDTO toDTO() {
        return new CreateDurationDTO(days);
    }
}
