

package com.sap.adds_service.prices.application.usecases.findprice;

import com.sap.adds_service.prices.application.output.FindPricePort;
import com.sap.adds_service.prices.domain.Price;
import com.sap.common_lib.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindPriceCaseTest {

    @Mock private FindPricePort findPricePort;
    @InjectMocks private FindPriceCase useCase;

    private static final UUID PRICE_ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();

    @Test
    void findById_shouldReturnPrice_whenExists() {
        // Arrange
        Price price = new Price(CINEMA_ID, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN);
        when(findPricePort.findById(PRICE_ID)).thenReturn(Optional.of(price));
        // Act
        Price result = useCase.findById(PRICE_ID);
        // Assert
        assertThat(result).isEqualTo(price);
        verify(findPricePort).findById(PRICE_ID);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        // Arrange
        when(findPricePort.findById(PRICE_ID)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(NotFoundException.class, () -> useCase.findById(PRICE_ID));
        verify(findPricePort).findById(PRICE_ID);
    }

    @Test
    void findByCinemaId_shouldReturnPrice_whenExists() {
        // Arrange
        Price price = new Price(CINEMA_ID, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN);
        when(findPricePort.findByCinemaId(CINEMA_ID)).thenReturn(Optional.of(price));
        // Act
        Price result = useCase.findByCinemaId(CINEMA_ID);
        // Assert
        assertThat(result).isEqualTo(price);
        verify(findPricePort).findByCinemaId(CINEMA_ID);
    }

    @Test
    void findByCinemaId_shouldThrow_whenNotFound() {
        // Arrange
        when(findPricePort.findByCinemaId(CINEMA_ID)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(NotFoundException.class, () -> useCase.findByCinemaId(CINEMA_ID));
        verify(findPricePort).findByCinemaId(CINEMA_ID);
    }

    @Test
    void findAll_shouldDelegateToPort() {
        // Arrange
        Page<Price> page = new PageImpl<>(List.of(new Price(CINEMA_ID, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE)));
        when(findPricePort.findAll(0)).thenReturn(page);
        // Act
        Page<Price> result = useCase.findAll(0);
        // Assert
        assertThat(result).isEqualTo(page);
        verify(findPricePort).findAll(0);
    }
}