package com.sap.adds_service.prices.application.input;

import com.sap.adds_service.prices.domain.Price;

import java.util.UUID;

public interface FindPriceCasePort {
    Price findById(UUID id);
    Price findByCinemaId(UUID cinemaId);
}
