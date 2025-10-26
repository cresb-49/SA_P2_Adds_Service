package com.sap.adds_service.adds.infrastructure.output.web;


import com.sap.adds_service.adds.application.output.FindUserPort;
import com.sap.adds_service.adds.domain.dtos.UserView;
import com.sap.adds_service.adds.infrastructure.output.web.mapper.UserViewMapper;
import com.sap.adds_service.common.infrastructure.output.web.port.UserGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SaleUserAdapter implements FindUserPort {

    private final UserGatewayPort userGatewayPort;
    private final UserViewMapper userViewMapper;

    @Override
    public boolean existsById(UUID userId) {
        return userGatewayPort.existsById(userId);
    }

    @Override
    public List<UserView> findByIds(List<UUID> ids) {
        var userResponseDTOs = userGatewayPort.findByIds(ids);
        return userViewMapper.toDomainList(userResponseDTOs);
    }

    @Override
    public UserView findById(UUID id) {
        var userResponseDTO = userGatewayPort.findById(id);
        return userViewMapper.toDomain(userResponseDTO);
    }
}
