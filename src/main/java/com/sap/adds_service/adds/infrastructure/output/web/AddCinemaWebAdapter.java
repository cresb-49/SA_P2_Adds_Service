package com.sap.adds_service.adds.infrastructure.output.web;

import com.sap.adds_service.adds.application.output.FindCinemaPort;
import com.sap.adds_service.adds.domain.dtos.CinemaView;
import com.sap.adds_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class AddCinemaWebAdapter implements FindCinemaPort {

    private final CinemaGatewayPort cinemaGatewayPort;

    @Override
    public boolean checkIfCinemaExistsById(UUID id) {
        return cinemaGatewayPort.checkIfCinemaExistsById(id);
    }

    @Override
    public CinemaView findById(UUID id) {
        return null;
    }

    @Override
    public List<CinemaView> findByIds(List<UUID> ids) {
        return List.of();
    }
}
