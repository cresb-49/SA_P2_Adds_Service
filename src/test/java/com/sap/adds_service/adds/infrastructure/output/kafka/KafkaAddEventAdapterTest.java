

package com.sap.adds_service.adds.infrastructure.output.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class KafkaAddEventAdapterTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaAddEventAdapter adapter;

    @Test
    void sendNotification_shouldComplete_withoutKafkaSend() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String message = "Test notification";
        // Act
        adapter.sendNotification(userId, message);
        // Assert
        verifyNoInteractions(kafkaTemplate);
    }
}