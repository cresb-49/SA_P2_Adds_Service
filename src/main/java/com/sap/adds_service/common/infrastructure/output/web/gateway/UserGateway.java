package com.sap.adds_service.common.infrastructure.output.web.gateway;


import com.sap.adds_service.common.infrastructure.output.web.port.UserGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UserGateway implements UserGatewayPort {

    @Override
    public boolean existsById(UUID userId) {
        return true;
    }

    @Override
    public Object findById(UUID id) {
        return null;
    }

    @Override
    public List<Object> findByIds(List<UUID> ids) {
        return List.of();
    }
}
