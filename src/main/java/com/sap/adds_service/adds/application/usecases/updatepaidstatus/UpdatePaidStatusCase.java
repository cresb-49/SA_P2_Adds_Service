package com.sap.adds_service.adds.application.usecases.updatepaidstatus;

import com.sap.adds_service.adds.application.input.UpdatePaidStatusCasePort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.application.output.SendNotificationPort;
import com.sap.adds_service.adds.application.usecases.updatepaidstatus.dtos.ChangePaidStateAddDTO;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.dto.NotificacionDTO;
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
            NotificacionDTO notificacionDTO = null;
            if (changePaidStateAddDTO.paid()) {
                add.markAsPaid();
                // Send notificaion to user
                notificacionDTO = new NotificacionDTO(); // Populate with success details
            } else {
                add.markAsFailed();
                // Send notificaion to user
                notificacionDTO = new NotificacionDTO(); // Populate with failure details
            }
            sendNotificationPort.sendNotification(notificacionDTO);
            //Save the add
            saveAddPort.save(add);
        } catch (IllegalStateException e) {
            // Send notification to user about failure
            System.out.println(e.getMessage());
        }
        // If ocurre other exception the transaction will be rollback and retry
    }
}
