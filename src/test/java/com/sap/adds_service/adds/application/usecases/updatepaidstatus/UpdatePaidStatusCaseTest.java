package com.sap.adds_service.adds.application.usecases.updatepaidstatus;

import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.application.output.SendNotificationPort;
import com.sap.adds_service.adds.application.usecases.updatepaidstatus.dtos.ChangePaidStateAddDTO;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.common_lib.exception.NonRetryableBusinessException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePaidStatusCaseTest {

    @Mock
    private FindingAddPort findingAddPort;
    @Mock
    private SaveAddPort saveAddPort;
    @Mock
    private SendNotificationPort sendNotificationPort;

    @InjectMocks
    private UpdatePaidStatusCase useCase;

    private static final UUID ADD_ID = UUID.randomUUID();

    private Add buildAdd(PaymentState state) {
        return new Add(
                ADD_ID,
                "content",
                AddType.TEXT_BANNER,
                "text/plain",
                false,
                null,
                true,
                "desc",
                UUID.randomUUID(),
                UUID.randomUUID(),
                state,
                null,
                BigDecimal.ONE,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @BeforeEach
    void setup() {
        lenient().when(saveAddPort.save(any(Add.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void updatePaidStatusEvent_shouldThrow_whenAddNotFound() {
        // Arrange
        ChangePaidStateAddDTO dto = new ChangePaidStateAddDTO(ADD_ID, true, "Test illegal state");
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.updatePaidStatusEvent(dto))
                .isInstanceOf(NonRetryableBusinessException.class);
        verifyNoInteractions(saveAddPort, sendNotificationPort);
    }

    @Test
    void updatePaidStatusEvent_shouldMarkAsPaid_andNotify() {
        // Arrange
        Add add = spy(buildAdd(PaymentState.PENDING));
        ChangePaidStateAddDTO dto = new ChangePaidStateAddDTO(ADD_ID, true, "Test illegal state");
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.of(add));

        // Act
        useCase.updatePaidStatusEvent(dto);

        // Assert
        verify(add).markAsPaid();
        verify(sendNotificationPort).sendNotification(any(UUID.class), any(String.class));
        verify(saveAddPort).save(add);
    }

    @Test
    void updatePaidStatusEvent_shouldMarkAsFailed_andNotify() {
        // Arrange
        Add add = spy(buildAdd(PaymentState.PENDING));
        ChangePaidStateAddDTO dto = new ChangePaidStateAddDTO(ADD_ID, false, "Test illegal state");
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.of(add));

        // Act
        useCase.updatePaidStatusEvent(dto);

        // Assert
        verify(add).markAsFailed();
        verify(sendNotificationPort).sendNotification(any(UUID.class),any(String.class));
        verify(saveAddPort).save(add);
    }

    @Test
    void updatePaidStatusEvent_shouldCatchIllegalStateException_andNotify() {
        // Arrange
        Add add = spy(buildAdd(PaymentState.PENDING));
        ChangePaidStateAddDTO dto = new ChangePaidStateAddDTO(ADD_ID, true, "Test illegal state");
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.of(add));
        doThrow(new IllegalStateException("Error")).when(add).markAsPaid();

        // Act
        useCase.updatePaidStatusEvent(dto);

        // Assert
        verify(sendNotificationPort).sendNotification(eq(ADD_ID), any(String.class));
        verify(saveAddPort, never()).save(any());
    }
}