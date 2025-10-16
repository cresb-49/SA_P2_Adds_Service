package com.sap.adds_service.adds.application.output;

import com.sap.adds_service.adds.application.usecases.findadd.dtos.AddFilter;
import com.sap.adds_service.adds.domain.Add;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindingAddPort {
    Optional<Add> findById(UUID id);

    Page<Add> findAll(int page);

    Page<Add> findByType(String type, int page);

    Page<Add> findByActive(boolean active, int page);

    Page<Add> findByCinemaId(UUID cinemaId, int page);

    Page<Add> findByUserId(UUID userId, int page);

    List<Add> findByIds(List<UUID> ids);

    Page<Add> findByFilers(AddFilter filter, int page);

    Optional<Add> findAddRandomByTypeAndCinemaId(String type, UUID cinemaId, String paymentState);

    Optional<Add> findAddRandomByTypeAndCinemaIdAndNow(String type, UUID cinemaId, String paymentState, LocalDateTime now);
}
