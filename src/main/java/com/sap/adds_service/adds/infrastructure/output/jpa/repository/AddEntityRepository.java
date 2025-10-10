package com.sap.adds_service.adds.infrastructure.output.jpa.repository;

import com.sap.adds_service.adds.infrastructure.output.jpa.entity.AddEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddEntityRepository extends JpaRepository<AddEntity, UUID>, JpaSpecificationExecutor<AddEntity> {
    Page<AddEntity> findByType(String type, Pageable pageable);

    Page<AddEntity> findByActive(boolean active, Pageable pageable);

    List<AddEntity> findByIdIn(List<UUID> ids);

    Page<AddEntity> findByCinemaId(UUID cinemaId, Pageable pageable);

    @Query(value = """
                SELECT *
                FROM add
                WHERE type = :type
                  AND cinema_id = :cinemaId
                  AND active = true
                ORDER BY random()
                LIMIT 1
            """, nativeQuery = true)
    Optional<AddEntity> findRandomByTypeAndCinemaId(@Param("type") String type, @Param("cinemaId") UUID cinemaId);
}
