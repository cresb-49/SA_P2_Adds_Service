package com.sap.adds_service.duration.infrastructure.input.domain.port;

import com.sap.adds_service.duration.domain.Duration;

import java.util.Optional;
import java.util.UUID;

public interface DurationGatewayPort {
    Optional<Duration> findById(UUID id);
}
