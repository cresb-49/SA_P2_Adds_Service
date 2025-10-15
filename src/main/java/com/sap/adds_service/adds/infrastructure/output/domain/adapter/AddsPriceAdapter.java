package com.sap.adds_service.adds.infrastructure.output.domain.adapter;

import com.sap.adds_service.adds.application.output.FindingPricePort;
import com.sap.adds_service.adds.domain.PriceView;
import com.sap.adds_service.adds.infrastructure.output.domain.mapper.PriceViewMapper;
import com.sap.adds_service.prices.infrastructure.input.domain.port.PriceGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class AddsPriceAdapter implements FindingPricePort {

    private final PriceGatewayPort priceGatewayPort;
    private final PriceViewMapper priceViewMapper;

    @Override
    public Optional<PriceView> findByCinemaId(UUID cinemaId) {
        var price = priceGatewayPort.findByCinemaId(cinemaId);
        return price.map(priceViewMapper::toDomain);
    }

    @Override
    public boolean checkIfCinemaExistsById(UUID cinemaId) {
        return priceGatewayPort.checkIfCinemaExistsById(cinemaId);
    }
}
