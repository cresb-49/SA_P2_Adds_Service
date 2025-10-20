package com.sap.adds_service.adds.application.usecases.retrypaid;

import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.application.output.SendNotificationPort;
import com.sap.adds_service.adds.application.output.SendNotificationAddPayment;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetryPaidAddCaseTest {

    @Mock
    private FindingAddPort findingAddPort;

    @Mock
    private SaveAddPort saveAddPort;

    @Mock
    private SendNotificationPort sendNotificationPort;

    @Mock
    private SendNotificationAddPayment sendNotificationAddPayment;

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
        when(saveAddPort.save(any(Add.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        retryPaidAddCase.retryPaidAdd(ADD_ID);

        // Assert
        verify(findingAddPort, times(1)).findById(ADD_ID);
        verify(sendNotificationPort, times(1)).sendNotification(any(), any());
        verify(saveAddPort, times(1)).save(add);
        verify(sendNotificationAddPayment, times(1)).sendPaymentEvent(any(), any(), any());
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
        verifyNoInteractions(sendNotificationPort, saveAddPort);
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
        verifyNoInteractions(sendNotificationPort, saveAddPort);
    }
}