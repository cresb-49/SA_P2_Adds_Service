package com.sap.adds_service.prices.application.input;

import java.util.UUID;

public interface DeletePriceCasePort {
    void deleteByCinemaId(UUID cinemaId);

    void deleteById(UUID id);
}
