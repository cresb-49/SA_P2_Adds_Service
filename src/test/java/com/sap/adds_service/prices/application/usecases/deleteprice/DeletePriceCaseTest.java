

package com.sap.adds_service.prices.application.usecases.deleteprice;

import com.sap.adds_service.prices.application.output.DeletePricePort;
import com.sap.adds_service.prices.application.output.FindPricePort;
import com.sap.adds_service.prices.domain.Price;
import com.sap.common_lib.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePriceCaseTest {

    @Mock private DeletePricePort deletePricePort;
    @Mock private FindPricePort findPricePort;

    @InjectMocks private DeletePriceCase useCase;

    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID PRICE_ID = UUID.randomUUID();

    @Test
    void deleteByCinemaId_shouldThrow_whenCinemaNotExists() {
        // Arrange
        when(findPricePort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(false);
        // Act & Assert
        assertThrows(NotFoundException.class, () -> useCase.deleteByCinemaId(CINEMA_ID));
        verify(findPricePort).checkIfCinemaExistsById(CINEMA_ID);
        verifyNoInteractions(deletePricePort);
    }

    @Test
    void deleteByCinemaId_shouldDelete_whenCinemaExists() {
        // Arrange
        when(findPricePort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(true);
        // Act
        useCase.deleteByCinemaId(CINEMA_ID);
        // Assert
        verify(findPricePort).checkIfCinemaExistsById(CINEMA_ID);
        verify(deletePricePort).deleteByCinemaId(CINEMA_ID);
    }

    @Test
    void deleteById_shouldThrow_whenPriceNotFound() {
        // Arrange
        when(findPricePort.findById(PRICE_ID)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(NotFoundException.class, () -> useCase.deleteById(PRICE_ID));
        verify(findPricePort).findById(PRICE_ID);
        verifyNoInteractions(deletePricePort);
    }

    @Test
    void deleteById_shouldDelete_whenPriceExists() {
        // Arrange
        when(findPricePort.findById(PRICE_ID)).thenReturn(Optional.of(mock(Price.class)));
        // Act
        useCase.deleteById(PRICE_ID);
        // Assert
        verify(findPricePort).findById(PRICE_ID);
        verify(deletePricePort).deleteById(PRICE_ID);
    }
}