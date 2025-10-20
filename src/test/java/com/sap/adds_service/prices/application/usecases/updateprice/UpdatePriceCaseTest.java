

package com.sap.adds_service.prices.application.usecases.updateprice;

import com.sap.adds_service.prices.application.output.FindPricePort;
import com.sap.adds_service.prices.application.output.SavePricePort;
import com.sap.adds_service.prices.application.usecases.updateprice.dtos.UpdatePriceDTO;
import com.sap.adds_service.prices.domain.Price;
import com.sap.common_lib.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePriceCaseTest {

    @Mock private FindPricePort findPricePort;
    @Mock private SavePricePort savePricePort;

    @InjectMocks private UpdatePriceCase useCase;

    private static final UUID PRICE_ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final BigDecimal NEW_TEXT = new BigDecimal("150.50");
    private static final BigDecimal NEW_VERTICAL = new BigDecimal("175.00");
    private static final BigDecimal NEW_HORIZONTAL = new BigDecimal("199.99");

    @Test
    void update_shouldThrow_whenPriceNotFound() {
        // Arrange
        UpdatePriceDTO dto = mock(UpdatePriceDTO.class);
        when(dto.id()).thenReturn(PRICE_ID);
        when(findPricePort.findById(PRICE_ID)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(NotFoundException.class, () -> useCase.update(dto));
        verify(findPricePort).findById(PRICE_ID);
        verifyNoInteractions(savePricePort);
    }

    @Test
    void update_shouldUpdateValidateAndSave_whenValid() {
        // Arrange
        Price existing = new Price(CINEMA_ID, BigDecimal.valueOf(10), BigDecimal.valueOf(20), BigDecimal.valueOf(30));
        UpdatePriceDTO dto = mock(UpdatePriceDTO.class);
        when(dto.id()).thenReturn(PRICE_ID);
        when(dto.amountTextBanner()).thenReturn(NEW_TEXT);
        when(dto.amountMediaVertical()).thenReturn(NEW_VERTICAL);
        when(dto.amountMediaHorizontal()).thenReturn(NEW_HORIZONTAL);
        when(findPricePort.findById(PRICE_ID)).thenReturn(Optional.of(existing));
        when(savePricePort.save(any(Price.class))).thenAnswer(inv -> inv.getArgument(0));
        // Act
        Price result = useCase.update(dto);
        // Assert
        assertThat(result.getAmountTextBanner()).isEqualByComparingTo(NEW_TEXT);
        assertThat(result.getAmountMediaVertical()).isEqualByComparingTo(NEW_VERTICAL);
        assertThat(result.getAmountMediaHorizontal()).isEqualByComparingTo(NEW_HORIZONTAL);
        verify(findPricePort).findById(PRICE_ID);
        verify(savePricePort).save(existing);
    }
}