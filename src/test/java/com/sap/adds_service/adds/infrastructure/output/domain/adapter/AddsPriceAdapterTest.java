
package com.sap.adds_service.adds.infrastructure.output.domain.adapter;

import com.sap.adds_service.adds.domain.PriceView;
import com.sap.adds_service.adds.infrastructure.output.domain.mapper.PriceViewMapper;
import com.sap.adds_service.prices.domain.Price;
import com.sap.adds_service.prices.infrastructure.input.domain.port.PriceGatewayPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddsPriceAdapterTest {

    @Mock
    private PriceGatewayPort priceGatewayPort;

    @Mock
    private PriceViewMapper priceViewMapper;

    @InjectMocks
    private AddsPriceAdapter adapter;

    private static final UUID CINEMA_ID = UUID.randomUUID();
    private Price price;
    private PriceView priceView;

    @BeforeEach
    void setup() {
        price = new Price(
                UUID.randomUUID(),
                CINEMA_ID,
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(30),
                LocalDateTime.now(),
                LocalDateTime.now());
        priceView = new PriceView(
                price.getId(),
                price.getCinemaId(),
                price.getAmountTextBanner(),
                price.getAmountMediaVertical(),
                price.getAmountMediaHorizontal(),
                price.getCreatedAt(),
                price.getUpdatedAt());
    }

    @Test
    void findByCinemaId_shouldReturnMappedPriceView_whenPriceExists() {
        // Arrange
        when(priceGatewayPort.findByCinemaId(CINEMA_ID)).thenReturn(Optional.of(price));
        when(priceViewMapper.toDomain(price)).thenReturn(priceView);

        // Act
        Optional<PriceView> result = adapter.findByCinemaId(CINEMA_ID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(priceView);
    }

    @Test
    void findByCinemaId_shouldReturnEmpty_whenPriceNotExists() {
        // Arrange
        when(priceGatewayPort.findByCinemaId(CINEMA_ID)).thenReturn(Optional.empty());

        // Act
        Optional<PriceView> result = adapter.findByCinemaId(CINEMA_ID);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void checkIfCinemaExistsById_shouldReturnTrue_whenCinemaExists() {
        // Arrange
        when(priceGatewayPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(true);

        // Act
        boolean result = adapter.checkIfCinemaExistsById(CINEMA_ID);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void checkIfCinemaExistsById_shouldReturnFalse_whenCinemaNotExists() {
        // Arrange
        when(priceGatewayPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(false);

        // Act
        boolean result = adapter.checkIfCinemaExistsById(CINEMA_ID);

        // Assert
        assertThat(result).isFalse();
    }
}