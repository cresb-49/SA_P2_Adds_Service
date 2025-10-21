package com.sap.adds_service.common.infrastructure.output.web.port;

import java.util.List;
import java.util.UUID;

public interface UserGatewayPort {
    boolean existsById(UUID userId);

    Object findById(UUID id);

    List<Object> findByIds(List<UUID> ids);
}
