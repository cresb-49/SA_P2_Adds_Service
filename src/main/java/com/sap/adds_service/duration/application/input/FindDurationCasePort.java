package com.sap.adds_service.duration.application.input;

import com.sap.adds_service.duration.domain.Duration;

import java.util.List;
import java.util.UUID;

public interface FindDurationCasePort {
    Duration findById(UUID id);
    List<Duration> findAll();
    List<Duration> findByIds(List<UUID> ids);
}
