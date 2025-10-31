package com.sap.adds_service.adds.infrastructure.output.web;

import com.sap.adds_service.adds.application.output.FindCinemaPort;
import com.sap.adds_service.adds.domain.dtos.CinemaView;
import com.sap.adds_service.adds.infrastructure.output.web.mapper.CinemaViewMapper;
import com.sap.adds_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class AddCinemaWebAdapter implements FindCinemaPort {

    private final CinemaGatewayPort cinemaGatewayPort;
    private final CinemaViewMapper cinemaViewMapper;

    @Override
    public boolean checkIfCinemaExistsById(UUID id) {
        return cinemaGatewayPort.checkIfCinemaExistsById(id);
    }

    @Override
    public CinemaView findById(UUID id) {
        var cinemaResponseDTO = cinemaGatewayPort.findById(id);
        return cinemaViewMapper.toDomain(cinemaResponseDTO);
    }

    @Override
    public List<CinemaView> findByIds(List<UUID> ids) {
        var cinemaResponseDTOs = cinemaGatewayPort.findByIds(ids);
        return cinemaViewMapper.toDomainList(cinemaResponseDTOs);
    }
}
