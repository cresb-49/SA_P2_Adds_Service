package com.sap.adds_service.prices.infrastructure.input.domain.port;

import com.sap.adds_service.prices.domain.Price;

import java.util.Optional;
import java.util.UUID;

public interface PriceGatewayPort {
    boolean checkIfCinemaExistsById(UUID cinemaId);

    Optional<Price> findByCinemaId(UUID cinemaId);
}
