package com.sap.adds_service.adds.application.input;

import com.sap.adds_service.adds.application.usecases.updatepaidstatus.dtos.ChangePaidStateAddDTO;

public interface UpdatePaidStatusCasePort {
    void updatePaidStatusEvent(ChangePaidStateAddDTO changePaidStateAddDTO);
}
