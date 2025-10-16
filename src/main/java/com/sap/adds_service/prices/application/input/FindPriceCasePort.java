package com.sap.adds_service.prices.application.input;

import com.sap.adds_service.prices.domain.Price;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface FindPriceCasePort {
    Price findById(UUID id);
    Price findByCinemaId(UUID cinemaId);
    Page<Price> findAll(int page);
}
