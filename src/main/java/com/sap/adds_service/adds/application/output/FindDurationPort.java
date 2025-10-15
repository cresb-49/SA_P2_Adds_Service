package com.sap.adds_service.adds.application.output;

import com.sap.adds_service.adds.domain.DurationView;

import java.util.Optional;
import java.util.UUID;

public interface FindDurationPort {
    Optional<DurationView> findById(UUID id);
}
