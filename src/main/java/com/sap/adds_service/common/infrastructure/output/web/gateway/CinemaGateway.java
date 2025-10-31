package com.sap.adds_service.common.infrastructure.output.web.gateway;

import com.sap.adds_service.common.infrastructure.output.dtos.CinemaResponseDTO;
import com.sap.adds_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CinemaGateway implements CinemaGatewayPort {
    private final WebClient.Builder webClient;
    private static final String USER_SERVICE_URL = "http://gateway/api/v1/cinemas";

    @Override
    public boolean checkIfCinemaExistsById(UUID id) {
        return Boolean.TRUE.equals(webClient.build()
                .get()
                .uri(USER_SERVICE_URL + "/public/" + id)
                .retrieve()
                .bodyToMono(CinemaResponseDTO.class)
                .map(cinema -> true)
                .defaultIfEmpty(false)
                .block());
    }

    @Override
    public List<CinemaResponseDTO> findByIds(List<UUID> ids) {
        return webClient.build()
                .post()
                .uri(USER_SERVICE_URL + "/public/by-ids")
                .bodyValue(new CinemasIdsRequestDTO(ids))
                .retrieve()
                .bodyToFlux(CinemaResponseDTO.class)
                .collectList()
                .block();
    }

    public record CinemasIdsRequestDTO(List<UUID> ids) {
    }

    @Override
    public CinemaResponseDTO findById(UUID id) {
        return webClient.build()
                .get()
                .uri(USER_SERVICE_URL + "/public/" + id)
                .retrieve()
                .bodyToMono(CinemaResponseDTO.class)
                .block();
    }
}
