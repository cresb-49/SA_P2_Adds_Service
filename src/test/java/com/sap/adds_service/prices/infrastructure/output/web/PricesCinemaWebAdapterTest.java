

package com.sap.adds_service.prices.infrastructure.output.web;

import com.sap.adds_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricesCinemaWebAdapterTest {

    @Mock
    private CinemaGatewayPort cinemaGatewayPort;

    @InjectMocks
    private PricesCinemaWebAdapter adapter;

    private static final UUID CINEMA_ID = UUID.randomUUID();

    @Test
    void checkIfCinemaExistsById_shouldReturnTrue_whenGatewayReturnsTrue() {
        // Arrange
        when(cinemaGatewayPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(true);
        // Act
        boolean result = adapter.checkIfCinemaExistsById(CINEMA_ID);
        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void checkIfCinemaExistsById_shouldReturnFalse_whenGatewayReturnsFalse() {
        // Arrange
        when(cinemaGatewayPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(false);
        // Act
        boolean result = adapter.checkIfCinemaExistsById(CINEMA_ID);
        // Assert
        assertThat(result).isFalse();
    }
}