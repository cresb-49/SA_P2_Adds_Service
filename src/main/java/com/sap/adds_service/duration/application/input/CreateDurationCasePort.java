package com.sap.adds_service.duration.application.input;

import com.sap.adds_service.duration.application.usecases.createduration.dtos.CreateDurationDTO;
import com.sap.adds_service.duration.domain.Duration;

public interface CreateDurationCasePort {
    Duration create(CreateDurationDTO createDurationDTO);
}
