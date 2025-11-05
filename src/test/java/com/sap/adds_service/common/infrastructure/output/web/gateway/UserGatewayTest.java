package com.sap.adds_service.common.infrastructure.output.web.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sap.common_lib.dto.response.users.profile.ProfileResponseDTO;
import com.sap.common_lib.dto.response.users.user.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserGatewayTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void existsById_shouldReturnTrueWhenServiceRespondsOk() {
        UUID userId = UUID.randomUUID();
        var gateway = gateway(request -> {
            assertThat(request.method()).isEqualTo(HttpMethod.HEAD);
            assertThat(request.url().toString()).endsWith("/" + userId);
            return Mono.just(ClientResponse.create(HttpStatus.OK).build());
        });

        boolean exists = gateway.existsById(userId);

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalseWhenNotFound() {
        UUID userId = UUID.randomUUID();
        var gateway = gateway(request -> Mono.just(ClientResponse.create(HttpStatus.NOT_FOUND).build()));

        boolean exists = gateway.existsById(userId);

        assertThat(exists).isFalse();
    }

    @Test
    void existsById_shouldReturnFalseWhenServiceErrors() {
        UUID userId = UUID.randomUUID();
        var error = WebClientResponseException.create(
                500,
                "Error",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );
        var gateway = gateway(request -> Mono.error(error));

        boolean exists = gateway.existsById(userId);

        assertThat(exists).isFalse();
    }

    @Test
    void findById_shouldReturnUserResponse() {
        UUID userId = UUID.randomUUID();
        var profile = new ProfileResponseDTO(UUID.randomUUID(), "Jane", "Doe");
        var expected = new UserResponseDTO(userId, "jane.doe@example.com", "ROLE_USER", profile);
        var gateway = gateway(request -> {
            assertThat(request.method()).isEqualTo(HttpMethod.GET);
            assertThat(request.url().toString()).endsWith("/" + userId);
            return Mono.just(jsonResponse(HttpStatus.OK, expected));
        });

        UserResponseDTO result = gateway.findById(userId);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void findByIds_shouldReturnUsersList() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        var users = List.of(
                new UserResponseDTO(firstId, "first@example.com", "ROLE_ADMIN",
                        new ProfileResponseDTO(UUID.randomUUID(), "Alice", "Smith")),
                new UserResponseDTO(secondId, "second@example.com", "ROLE_USER",
                        new ProfileResponseDTO(UUID.randomUUID(), "Bob", "Johnson"))
        );
        var gateway = gateway(request -> {
            assertThat(request.method()).isEqualTo(HttpMethod.POST);
            assertThat(request.url().toString()).endsWith("/by-ids");
            return Mono.just(jsonResponse(HttpStatus.OK, users));
        });

        List<UserResponseDTO> result = gateway.findByIds(List.of(firstId, secondId));

        assertThat(result).containsExactlyElementsOf(users);
    }

    private UserGateway gateway(java.util.function.Function<ClientRequest, Mono<ClientResponse>> handler) {
        WebClient.Builder builder = WebClient.builder()
                .exchangeFunction(handler::apply);
        return new UserGateway(builder);
    }

    private ClientResponse jsonResponse(HttpStatus status, Object body) {
        try {
            return ClientResponse.create(status)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(objectMapper.writeValueAsString(body))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize body", e);
        }
    }
}
