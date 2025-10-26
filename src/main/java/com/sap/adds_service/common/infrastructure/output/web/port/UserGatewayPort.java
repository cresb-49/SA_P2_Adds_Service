package com.sap.adds_service.common.infrastructure.output.web.port;

import com.sap.common_lib.dto.response.users.user.UserResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserGatewayPort {
    boolean existsById(UUID userId);

    UserResponseDTO findById(UUID id);

    List<UserResponseDTO> findByIds(List<UUID> ids);
}
