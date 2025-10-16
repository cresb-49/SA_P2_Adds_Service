package com.sap.adds_service.adds.application.usecases.updatepaidstatus;

import com.sap.adds_service.adds.application.input.UpdatePaidStatusCasePort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
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

    @Override
    public void updatePaidStatusEvent(ChangePaidStateAddDTO changePaidStateAddDTO) {
        Add add = findingAddPort.findById(changePaidStateAddDTO.addId()).orElseThrow(
                () -> new NonRetryableBusinessException("Add with ID: " + changePaidStateAddDTO.addId() + " not found")
        );
        try {
            if (changePaidStateAddDTO.paid()) {
                add.markAsPaid();
                // Send notificaion to user
            } else {
                add.markAsFailed();
                // Send notificaion to user
            }
            //Save the add
            saveAddPort.save(add);
        } catch (IllegalStateException e) {
            // Send notification to user about failure
        }
        // If ocurre other exception the transaction will be rollback and retry
    }
}
