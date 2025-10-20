package com.sap.adds_service.prices.infrastructure.output.web;

import com.sap.adds_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import com.sap.adds_service.prices.application.output.FindCinemaPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class PricesCinemaWebAdapter implements FindCinemaPort {

    private final CinemaGatewayPort cinemaGatewayPort;

    @Override
    public boolean checkIfCinemaExistsById(UUID id) {
        return cinemaGatewayPort.checkIfCinemaExistsById(id);
    }
}
