package com.sap.adds_service.common.infrastructure.output.web.port;

import com.sap.adds_service.common.infrastructure.output.dtos.CinemaResponseDTO;

import java.util.List;

public interface CinemaGatewayPort {
    boolean checkIfCinemaExistsById(java.util.UUID id);
    List<CinemaResponseDTO> findByIds(List<java.util.UUID> ids);
    CinemaResponseDTO findById(java.util.UUID id);
}
