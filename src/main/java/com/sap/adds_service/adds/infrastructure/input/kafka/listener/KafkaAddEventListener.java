package com.sap.adds_service.adds.infrastructure.input.kafka.listener;

import com.sap.adds_service.adds.application.input.UpdatePaidStatusCasePort;
import com.sap.adds_service.adds.application.usecases.updatepaidstatus.dtos.ChangePaidStateAddDTO;
import com.sap.common_lib.dto.response.add.events.ChangePaidStateAddEventDTO;
import com.sap.common_lib.events.groups.GroupsConstants;
import com.sap.common_lib.events.topics.TopicConstants;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaAddEventListener {

    private final UpdatePaidStatusCasePort updatePaidStatusCasePort;

    @KafkaListener(topics = TopicConstants.UPDATE_PAID_STATUS_ADD_TOPIC, groupId = GroupsConstants.ADDS_SERVICE_GROUP_ID)
    public void onUpdatePaidStatusEvent(@Payload ChangePaidStateAddEventDTO changePaidStateAddEventDTO) {
        var appDto = new ChangePaidStateAddDTO(
                changePaidStateAddEventDTO.addId(),
                changePaidStateAddEventDTO.paid(),
                changePaidStateAddEventDTO.message()
        );
        System.out.println("Received Kafka message: " + appDto.toString());
        updatePaidStatusCasePort.updatePaidStatusEvent(appDto);
    }
}
