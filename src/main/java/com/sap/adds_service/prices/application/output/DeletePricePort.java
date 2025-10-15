package com.sap.adds_service.prices.application.output;

import java.util.UUID;

public interface DeletePricePort {
    void deleteByCinemaId(UUID cinemaId);

    void deleteById(UUID id);
}
