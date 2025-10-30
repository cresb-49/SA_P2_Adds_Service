package com.sap.adds_service.adds.infrastructure.output.kafka;

import com.sap.adds_service.adds.application.output.SendNotificationAddPayment;
import com.sap.adds_service.adds.application.output.SendNotificationPort;
import com.sap.common_lib.dto.response.add.events.AddPendingPaymentEventDTO;
import com.sap.common_lib.dto.response.notification.events.SendGenericMailEventDTO;
import com.sap.common_lib.events.topics.TopicConstants;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@AllArgsConstructor
public class KafkaAddEventAdapter implements SendNotificationPort, SendNotificationAddPayment {

    private final KafkaTemplate<String, AddPendingPaymentEventDTO> pendingPaymentEventDTOKafkaTemplate;
    private final KafkaTemplate<String, SendGenericMailEventDTO> sendGenericMailEventDTOKafkaTemplate;

    @Override
    public void sendPaymentEvent(UUID addId, UUID userId, UUID cinemaId, BigDecimal amount) {
        var eventDTO = new AddPendingPaymentEventDTO(
                addId,
                userId,
                cinemaId,
                amount
        );
        pendingPaymentEventDTOKafkaTemplate.send(TopicConstants.ADDS_PENDING_PAYMENT_TOPIC, addId.toString(), eventDTO);
    }

    @Override
    public void sendNotification(UUID userId, String message) {
        var eventDTO = new SendGenericMailEventDTO(
                userId.toString(),
                message
        );
        sendGenericMailEventDTOKafkaTemplate.send(TopicConstants.REQUEST_GENERIC_MAIL, userId.toString(), eventDTO);
    }
}
