

package com.sap.adds_service.prices.infrastructure.input.domain.gateway;

import com.sap.adds_service.prices.domain.Price;
import com.sap.adds_service.prices.infrastructure.output.jpa.adapater.PriceJpaAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceGatewayTest {

    @Mock
    private PriceJpaAdapter priceJpaAdapter;

    @InjectMocks
    private PriceGateway gateway;

    private static final UUID CINEMA_ID = UUID.randomUUID();

    @Test
    void checkIfCinemaExistsById_shouldDelegateAndReturnTrue() {
        // Arrange
        when(priceJpaAdapter.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(true);
        // Act
        boolean result = gateway.checkIfCinemaExistsById(CINEMA_ID);
        // Assert
        assertThat(result).isTrue();
        verify(priceJpaAdapter).checkIfCinemaExistsById(CINEMA_ID);
    }

    @Test
    void findByCinemaId_shouldReturnPrice_whenExists() {
        // Arrange
        Price price = new Price(CINEMA_ID, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN);
        when(priceJpaAdapter.findByCinemaId(CINEMA_ID)).thenReturn(Optional.of(price));
        // Act
        Optional<Price> result = gateway.findByCinemaId(CINEMA_ID);
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(price);
        verify(priceJpaAdapter).findByCinemaId(CINEMA_ID);
    }

    @Test
    void findByCinemaId_shouldReturnEmpty_whenNotExists() {
        // Arrange
        when(priceJpaAdapter.findByCinemaId(CINEMA_ID)).thenReturn(Optional.empty());
        // Act
        Optional<Price> result = gateway.findByCinemaId(CINEMA_ID);
        // Assert
        assertThat(result).isEmpty();
        verify(priceJpaAdapter).findByCinemaId(CINEMA_ID);
    }
}