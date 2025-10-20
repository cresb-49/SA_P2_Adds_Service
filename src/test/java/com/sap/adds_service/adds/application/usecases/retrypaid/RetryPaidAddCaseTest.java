

package com.sap.adds_service.adds.application.usecases.retrypaid;

import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.application.output.SendNotificationPort;
import com.sap.adds_service.adds.application.output.SendPaymentAddPort;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.common_lib.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class RetryPaidAddCaseTest {

    @Mock
    private FindingAddPort findingAddPort;

    @Mock
    private SaveAddPort saveAddPort;

    @Mock
    private SendPaymentAddPort sendPaymentAddPort;

    @Mock
    private SendNotificationPort sendNotificationPort;

    @InjectMocks
    private RetryPaidAddCase retryPaidAddCase;

    private static final UUID ADD_ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void retryPaidAdd_shouldSucceed_whenAddExistsAndIsFailed() {
        // Arrange
        Add add = new Add(
                UUID.randomUUID(),
                "contenido",
                com.sap.adds_service.adds.domain.AddType.TEXT_BANNER,
                "text/plain",
                false,
                null,
                false,
                "descripcion",
                CINEMA_ID,
                USER_ID,
                PaymentState.FAILED,
                null,
                BigDecimal.TEN,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.of(add));

        // Act
        retryPaidAddCase.retryPaidAdd(ADD_ID);

        // Assert
        verify(findingAddPort, times(1)).findById(ADD_ID);
        verify(sendPaymentAddPort, times(1)).sendPaymentEvent(any());
        verify(sendNotificationPort, times(1)).sendNotification(any());
        verify(saveAddPort, times(1)).save(add);
        org.assertj.core.api.Assertions.assertThat(add.getPaymentState()).isEqualTo(PaymentState.PENDING);
        org.assertj.core.api.Assertions.assertThat(add.isActive()).isFalse();
    }

    @Test
    void retryPaidAdd_shouldThrow_whenAddNotFound() {
        // Arrange
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> retryPaidAddCase.retryPaidAdd(ADD_ID))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(sendPaymentAddPort, sendNotificationPort, saveAddPort);
    }

    @Test
    void retryPaidAdd_shouldThrow_whenAddIsNotFailed() {
        // Arrange
        Add add = new Add(
                UUID.randomUUID(),
                "contenido",
                com.sap.adds_service.adds.domain.AddType.TEXT_BANNER,
                "text/plain",
                false,
                null,
                false,
                "descripcion",
                CINEMA_ID,
                USER_ID,
                PaymentState.COMPLETED,
                LocalDateTime.now(),
                BigDecimal.TEN,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.of(add));

        // Act & Assert
        assertThatThrownBy(() -> retryPaidAddCase.retryPaidAdd(ADD_ID))
                .isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(sendPaymentAddPort, sendNotificationPort, saveAddPort);
    }
}