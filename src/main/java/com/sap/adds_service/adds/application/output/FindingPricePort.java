package com.sap.adds_service.adds.application.output;

import com.sap.adds_service.adds.domain.PriceView;

import java.util.Optional;
import java.util.UUID;

public interface FindingPricePort {
    Optional<PriceView> findByCinemaId(UUID cinemaId);
    boolean checkIfCinemaExistsById(UUID cinemaId);
}
