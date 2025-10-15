package com.sap.adds_service.duration.infrastructure.output.jpa.repository;

import com.sap.adds_service.duration.infrastructure.output.jpa.entity.DurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DurationEntityRepository extends JpaRepository<DurationEntity, UUID> {
    List<DurationEntity> findByIdIn(List<UUID> ids);

    boolean existsByDays(int daysDuration);
}
