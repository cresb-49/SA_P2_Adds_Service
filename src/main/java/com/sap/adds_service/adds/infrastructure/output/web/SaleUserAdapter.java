package com.sap.adds_service.adds.infrastructure.output.web;


import com.sap.adds_service.adds.application.output.FindUserPort;
import com.sap.adds_service.common.infrastructure.output.web.port.UserGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class SaleUserAdapter implements FindUserPort {
    private final UserGatewayPort userGatewayPort;
    @Override
    public boolean existsById(UUID userId) {
        return userGatewayPort.existsById(userId);
    }
}
