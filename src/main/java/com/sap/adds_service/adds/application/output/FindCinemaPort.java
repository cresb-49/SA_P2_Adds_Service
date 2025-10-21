package com.sap.adds_service.adds.application.output;

import com.sap.adds_service.adds.domain.dtos.CinemaView;

import java.util.List;
import java.util.UUID;

public interface FindCinemaPort {
    boolean checkIfCinemaExistsById(UUID id);

    CinemaView findById(UUID id);

    List<CinemaView> findByIds(List<UUID> ids);
}
