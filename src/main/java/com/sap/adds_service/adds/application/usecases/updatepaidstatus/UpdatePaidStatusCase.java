package com.sap.adds_service.adds.application.usecases.updatepaidstatus;

import com.sap.adds_service.adds.application.input.UpdatePaidStatusCasePort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.application.output.SendNotificationPort;
import com.sap.adds_service.adds.application.usecases.updatepaidstatus.dtos.ChangePaidStateAddDTO;
import com.sap.adds_service.adds.domain.Add;
import com.sap.common_lib.exception.NonRetryableBusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class UpdatePaidStatusCase implements UpdatePaidStatusCasePort {

    private final FindingAddPort findingAddPort;
    private final SaveAddPort saveAddPort;
    private final SendNotificationPort sendNotificationPort;

    @Override
    public void updatePaidStatusEvent(ChangePaidStateAddDTO changePaidStateAddDTO) {
        Add add = findingAddPort.findById(changePaidStateAddDTO.addId()).orElseThrow(
                () -> new NonRetryableBusinessException("Anuncio no encontrado")
        );
        try {
            String message = null;
            if (changePaidStateAddDTO.paid()) {
                add.markAsPaid();
                message = "Su anuncio fue pagado con exito!!!, ID: " + add.getId();
            } else {
                add.markAsFailed();
                message = "Su anuncio tubo un error de pago!!!, ID: " + add.getId() + ", " + changePaidStateAddDTO.message();
            }
            sendNotificationPort.sendNotification(add.getId(), message);
            //Save the add
            saveAddPort.save(add);
        } catch (IllegalStateException e) {
            // Send notification to user about failure
            sendNotificationPort.sendNotification(add.getId(), e.getMessage());
        }
        // If ocurre other exception the transaction will be rollback and retry
    }
}
