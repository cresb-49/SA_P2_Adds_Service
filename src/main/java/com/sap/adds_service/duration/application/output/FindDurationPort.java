package com.sap.adds_service.duration.application.output;

import com.sap.adds_service.duration.domain.Duration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindDurationPort {
    Optional<Duration> findById(UUID id);

    List<Duration> findAll();

    List<Duration> findByIds(List<UUID> ids);

    boolean existsByDaysDuration(int daysDuration);
}
