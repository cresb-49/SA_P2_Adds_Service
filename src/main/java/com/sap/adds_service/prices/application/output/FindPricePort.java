package com.sap.adds_service.prices.application.output;

import com.sap.adds_service.prices.domain.Price;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindPricePort {
    Optional<Price> findById(UUID id);

    boolean checkIfCinemaExistsById(UUID id);

    Optional<Price> findByCinemaId(UUID cinemaId);

    Page<Price> findAll(int page);
}
