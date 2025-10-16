package com.sap.adds_service.adds.infrastructure.output.kafka;

import com.sap.adds_service.adds.application.output.SendNotificationPort;
import com.sap.adds_service.adds.application.output.SendPaymentAddPort;
import com.sap.adds_service.adds.domain.dto.NotificacionDTO;
import com.sap.adds_service.adds.domain.dto.PaidAddDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaAddEventAdapter implements SendNotificationPort, SendPaymentAddPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Override
    public void sendPaymentEvent(PaidAddDTO paidAddDTO) {
        System.out.println("Sending Kafka message: " + paidAddDTO);
    }

    @Override
    public void sendNotification(NotificacionDTO notificacionDTO) {
        System.out.println("Sending Kafka message: " + notificacionDTO);
    }
}
