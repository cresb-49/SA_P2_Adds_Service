package com.sap.adds_service.adds.application.usecases.retrypaid;

import com.sap.adds_service.adds.application.input.RetryPaidAddCasePort;
import com.sap.adds_service.adds.application.output.*;
import com.sap.adds_service.adds.domain.Add;
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
    private final SendNotificationPort sendNotificationPort;
    private final SendNotificationAddPayment sendNotificationAddPayment;

    @Override
    public void retryPaidAdd(UUID addId) {
        Add add = findAddPort.findById(addId).orElseThrow(() -> new NotFoundException(
                "No se encontró el anuncio con ID: " + addId
        ));
        //Change status to active and reset payment attempts
        add.retryPayment();
        //Validate and save
        add.validate();
        // Save add
        var savedAdd = saveAddPort.save(add);
        //Send payment event
        sendNotificationAddPayment.sendPaymentEvent(savedAdd.getId(), savedAdd.getUserId(), savedAdd.getPrice());
        //Send notification event
        sendNotificationPort.sendNotification(savedAdd.getUserId(), "Se ha reintentado el pago del anuncio con ID: " + savedAdd.getId());
    }
}
