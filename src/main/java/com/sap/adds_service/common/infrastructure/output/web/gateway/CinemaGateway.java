package com.sap.adds_service.common.infrastructure.output.web.gateway;

import com.sap.adds_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CinemaGateway implements CinemaGatewayPort {
    @Override
    public boolean checkIfCinemaExistsById(UUID id) {
        return true;
    }

    @Override
    public List<Object> findByIds(List<UUID> ids) {
        return List.of();
    }

    @Override
    public Object findById(UUID id) {
        return null;
    }
}
