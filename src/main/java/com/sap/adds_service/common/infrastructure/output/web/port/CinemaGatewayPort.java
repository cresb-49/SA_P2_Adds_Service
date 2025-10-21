package com.sap.adds_service.common.infrastructure.output.web.port;

import java.util.List;

public interface CinemaGatewayPort {
    boolean checkIfCinemaExistsById(java.util.UUID id);
    List<Object> findByIds(List<java.util.UUID> ids);
    Object findById(java.util.UUID id);
}
