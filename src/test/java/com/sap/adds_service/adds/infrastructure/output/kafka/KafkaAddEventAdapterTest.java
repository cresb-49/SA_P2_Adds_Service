

package com.sap.adds_service.adds.infrastructure.output.kafka;

import com.sap.adds_service.adds.domain.dto.NotificacionDTO;
import com.sap.adds_service.adds.domain.dto.PaidAddDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class KafkaAddEventAdapterTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaAddEventAdapter adapter;

    @Test
    void sendPaymentEvent_shouldComplete_withoutKafkaSend() {
        // Arrange
        PaidAddDTO dto = new PaidAddDTO();
        // Act
        adapter.sendPaymentEvent(dto);
        // Assert
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void sendNotification_shouldComplete_withoutKafkaSend() {
        // Arrange
        NotificacionDTO dto = new NotificacionDTO();
        // Act
        adapter.sendNotification(dto);
        // Assert
        verifyNoInteractions(kafkaTemplate);
    }
}