

package com.sap.adds_service.adds.infrastructure.input.kafka.listener;

import com.sap.adds_service.adds.application.input.UpdatePaidStatusCasePort;
import com.sap.adds_service.adds.application.usecases.updatepaidstatus.dtos.ChangePaidStateAddDTO;
import com.sap.common_lib.dto.response.add.events.ChangePaidStateAddEventDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaAddEventListenerTest {

    @Mock
    private UpdatePaidStatusCasePort updatePaidStatusCasePort;

    @InjectMocks
    private KafkaAddEventListener listener;

    @Test
    void onUpdatePaidStatusEvent_shouldConvertAndDelegateToUseCase() {
        // Arrange
        UUID addId = UUID.randomUUID();
        ChangePaidStateAddEventDTO eventDTO = new ChangePaidStateAddEventDTO(addId, true, "Pago completado");

        // Act
        listener.onUpdatePaidStatusEvent(eventDTO);

        // Assert
        ArgumentCaptor<ChangePaidStateAddDTO> captor = ArgumentCaptor.forClass(ChangePaidStateAddDTO.class);
        verify(updatePaidStatusCasePort).updatePaidStatusEvent(captor.capture());
        ChangePaidStateAddDTO captured = captor.getValue();

        assertThat(captured.addId()).isEqualTo(addId);
        assertThat(captured.paid()).isTrue();
        assertThat(captured.message()).isEqualTo("Pago completado");
    }
}