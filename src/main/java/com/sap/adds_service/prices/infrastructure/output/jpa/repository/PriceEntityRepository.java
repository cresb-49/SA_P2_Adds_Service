package com.sap.adds_service.prices.infrastructure.output.jpa.repository;

import com.sap.adds_service.prices.infrastructure.output.jpa.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceEntityRepository extends JpaRepository<PriceEntity, UUID> {

    boolean existsByCinemaId(UUID cinemaId);

    Optional<PriceEntity> findByCinemaId(UUID cinemaId);

    void deleteByCinemaId(UUID cinemaId);
}
