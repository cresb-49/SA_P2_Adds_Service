package com.sap.adds_service.common.infrastructure.output.web.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sap.adds_service.common.infrastructure.output.dtos.CinemaResponseDTO;
import com.sap.adds_service.common.infrastructure.output.dtos.CompanyResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CinemaGatewayTest {

    private static final UUID CINEMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void checkIfCinemaExistsById_shouldReturnTrueWhenBodyPresent() {
        var expected = cinemaResponse(CINEMA_ID, "Main Cinema");
        var gateway = gateway(request -> {
            assertThat(request.method()).isEqualTo(HttpMethod.GET);
            assertThat(request.url().toString()).endsWith("/public/" + CINEMA_ID);
            return Mono.just(jsonResponse(HttpStatus.OK, expected));
        });

        boolean exists = gateway.checkIfCinemaExistsById(CINEMA_ID);

        assertThat(exists).isTrue();
    }

    @Test
    void checkIfCinemaExistsById_shouldReturnFalseWhenBodyMissing() {
        var gateway = gateway(request -> Mono.just(ClientResponse.create(HttpStatus.OK).build()));

        boolean exists = gateway.checkIfCinemaExistsById(CINEMA_ID);

        assertThat(exists).isFalse();
    }

    @Test
    void findByIds_shouldReturnCinemasList() {
        var secondId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        var cinemas = List.of(
                cinemaResponse(CINEMA_ID, "Main Cinema"),
                cinemaResponse(secondId, "Secondary Cinema")
        );
        var gateway = gateway(request -> {
            assertThat(request.method()).isEqualTo(HttpMethod.POST);
            assertThat(request.url().toString()).endsWith("/public/by-ids");
            return Mono.just(jsonResponse(HttpStatus.OK, cinemas));
        });

        var result = gateway.findByIds(List.of(CINEMA_ID, secondId));

        assertThat(result).containsExactlyElementsOf(cinemas);
    }

    @Test
    void findById_shouldReturnCinema() {
        var expected = cinemaResponse(CINEMA_ID, "Main Cinema");
        var gateway = gateway(request -> {
            assertThat(request.method()).isEqualTo(HttpMethod.GET);
            assertThat(request.url().toString()).endsWith("/public/" + CINEMA_ID);
            return Mono.just(jsonResponse(HttpStatus.OK, expected));
        });

        var result = gateway.findById(CINEMA_ID);

        assertThat(result).isEqualTo(expected);
    }

    private CinemaGateway gateway(java.util.function.Function<ClientRequest, Mono<ClientResponse>> handler) {
        WebClient.Builder builder = WebClient.builder()
                .exchangeFunction(handler::apply);
        return new CinemaGateway(builder);
    }

    private ClientResponse jsonResponse(HttpStatus status, Object body) {
        try {
            return ClientResponse.create(status)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(objectMapper.writeValueAsString(body))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize response body", e);
        }
    }

    private CinemaResponseDTO cinemaResponse(UUID id, String name) {
        var company = new CompanyResponseDTO(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Company",
                "123 Main St",
                "555-1234"
        );
        return new CinemaResponseDTO(
                id,
                company,
                name,
                BigDecimal.TEN,
                LocalDate.of(2024, 1, 1)
        );
    }
}
