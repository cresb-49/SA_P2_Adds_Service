package com.sap.adds_service.adds.application.usecases.retrypaid;

import com.sap.adds_service.adds.application.input.RetryPaidAddCasePort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.application.output.SendNotificationPort;
import com.sap.adds_service.adds.application.output.SendPaymentAddPort;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.dto.NotificacionDTO;
import com.sap.adds_service.adds.domain.dto.PaidAddDTO;
import com.sap.common_lib.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class RetryPaidAddCase implements RetryPaidAddCasePort {

    private final FindingAddPort findAddPort;
    private final SaveAddPort saveAddPort;
    private final SendPaymentAddPort sendPaymentAddPort;
    private final SendNotificationPort sendNotificationPort;

    @Override
    public void retryPaidAdd(UUID addId) {
        Add add = findAddPort.findById(addId).orElseThrow(() -> new NotFoundException(
                "No se encontró el anuncio con ID: " + addId
        ));
        //Change status to active and reset payment attempts
        add.retryPayment();
        //Validate and save
        add.validate();
        //Send payment event
        sendPaymentAddPort.sendPaymentEvent(new PaidAddDTO());
        //Send notification event
        sendNotificationPort.sendNotification(new NotificacionDTO());
        saveAddPort.save(add);
    }
}
