package com.sap.adds_service.adds.infrastructure.output.jpa.repository;

import com.sap.adds_service.adds.infrastructure.output.jpa.entity.AddEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface AddEntityRepository extends JpaRepository<AddEntity, UUID>, JpaSpecificationExecutor<AddEntity> {
    Page<AddEntity> findByType(String type, Pageable pageable);

    Page<AddEntity> findByActive(boolean active, Pageable pageable);

    List<AddEntity> findByIdIn(List<UUID> ids);

    Page<AddEntity> findByCinemaId(UUID cinemaId, Pageable pageable);
}
