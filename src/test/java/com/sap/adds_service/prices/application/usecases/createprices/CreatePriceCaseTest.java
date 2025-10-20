

package com.sap.adds_service.prices.application.usecases.createprices;

import com.sap.adds_service.prices.application.output.FindCinemaPort;
import com.sap.adds_service.prices.application.output.FindPricePort;
import com.sap.adds_service.prices.application.output.SavePricePort;
import com.sap.adds_service.prices.domain.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePriceCaseTest {

    @Mock private FindPricePort findPricePort;
    @Mock private SavePricePort savePricePort;
    @Mock private FindCinemaPort findCinemaPort;

    @InjectMocks private CreatePriceCase useCase;

    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(200.00);

    @BeforeEach
    void init() {}

    @Test
    void createPrices_shouldThrow_whenCinemaNotExists() {
        // Arrange
        when(findCinemaPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(false);
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.createPrices(CINEMA_ID));
        verify(findCinemaPort).checkIfCinemaExistsById(CINEMA_ID);
        verifyNoInteractions(findPricePort, savePricePort);
    }

    @Test
    void createPrices_shouldThrow_whenPricesAlreadyExist() {
        // Arrange
        when(findCinemaPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(true);
        when(findPricePort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(true);
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.createPrices(CINEMA_ID));
        verify(findCinemaPort).checkIfCinemaExistsById(CINEMA_ID);
        verify(findPricePort).checkIfCinemaExistsById(CINEMA_ID);
        verifyNoInteractions(savePricePort);
    }

    @Test
    void createPrices_shouldSaveAndReturn_whenValid() {
        // Arrange
        when(findCinemaPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(true);
        when(findPricePort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(false);
        when(savePricePort.save(any(Price.class))).thenAnswer(inv -> inv.getArgument(0));
        // Act
        Price result = useCase.createPrices(CINEMA_ID);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCinemaId()).isEqualTo(CINEMA_ID);
        assertThat(result.getAmountTextBanner()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(result.getAmountMediaVertical()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(result.getAmountMediaHorizontal()).isEqualByComparingTo(DEFAULT_PRICE);
        verify(findCinemaPort).checkIfCinemaExistsById(CINEMA_ID);
        verify(findPricePort).checkIfCinemaExistsById(CINEMA_ID);
        verify(savePricePort).save(any(Price.class));
    }
}