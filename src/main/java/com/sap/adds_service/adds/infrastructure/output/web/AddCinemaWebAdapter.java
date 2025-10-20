package com.sap.adds_service.adds.infrastructure.output.web;

import com.sap.adds_service.adds.application.output.FindCinemaPort;
import com.sap.adds_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class AddCinemaWebAdapter implements FindCinemaPort {

    private final CinemaGatewayPort cinemaGatewayPort;

    @Override
    public boolean checkIfCinemaExistsById(UUID id) {
        return cinemaGatewayPort.checkIfCinemaExistsById(id);
    }
}
