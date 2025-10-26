package com.sap.adds_service.common.infrastructure.output.web.gateway;


import com.sap.adds_service.common.infrastructure.output.web.port.UserGatewayPort;
import com.sap.common_lib.dto.response.users.user.UserIdsRequestDTO;
import com.sap.common_lib.dto.response.users.user.UserResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UserGateway implements UserGatewayPort {

    private final WebClient.Builder webClient;
    private static final String USER_SERVICE_URL = "http://gateway/api/v1/users";

    @Override
    public boolean existsById(UUID userId) {
        boolean exists = Boolean.TRUE.equals(webClient.build()
                .head()
                .uri(USER_SERVICE_URL + "/{id}", userId)
                .exchangeToMono(resp -> Mono.just(resp.statusCode().is2xxSuccessful()))
                .onErrorReturn(WebClientResponseException.NotFound.class, false)
                .onErrorReturn(WebClientResponseException.class, false)
                .block());
        return exists;
    }

    @Override
    public UserResponseDTO findById(UUID id) {
        return webClient.build()
                .get()
                .uri(USER_SERVICE_URL + "/" + id)
                .retrieve()
                .bodyToMono(UserResponseDTO.class)
                .block();
    }

    @Override
    public List<UserResponseDTO> findByIds(List<UUID> ids) {
        return webClient.build()
                .post()
                .uri(USER_SERVICE_URL + "/by-ids")
                .bodyValue(new UserIdsRequestDTO(ids))
                .retrieve()
                .bodyToFlux(UserResponseDTO.class)
                .collectList()
                .block();
    }
}
