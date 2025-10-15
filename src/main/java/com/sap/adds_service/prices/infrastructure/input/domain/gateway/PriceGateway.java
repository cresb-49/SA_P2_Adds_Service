package com.sap.adds_service.prices.infrastructure.input.domain.gateway;

import com.sap.adds_service.prices.domain.Price;
import com.sap.adds_service.prices.infrastructure.input.domain.port.PriceGatewayPort;
import com.sap.adds_service.prices.infrastructure.output.jpa.adapater.PriceJpaAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class PriceGateway implements PriceGatewayPort {

    private final PriceJpaAdapter priceJpaAdapter;

    @Override
    public boolean checkIfCinemaExistsById(UUID cinemaId) {
        return priceJpaAdapter.checkIfCinemaExistsById(cinemaId);
    }

    @Override
    public Optional<Price> findByCinemaId(UUID cinemaId) {
        return priceJpaAdapter.findByCinemaId(cinemaId);
    }
}
