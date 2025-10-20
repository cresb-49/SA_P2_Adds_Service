package com.sap.adds_service.common.infrastructure.output.web.gateway;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CinemaGatewayTest {

    private static final UUID ANY_ID = UUID.randomUUID();

    @Test
    void checkIfCinemaExistsById_shouldReturnTrue_always() {
        // Arrange
        CinemaGateway gateway = new CinemaGateway();
        // Act
        boolean result = gateway.checkIfCinemaExistsById(ANY_ID);
        // Assert
        assertThat(result).isTrue();
    }
}
