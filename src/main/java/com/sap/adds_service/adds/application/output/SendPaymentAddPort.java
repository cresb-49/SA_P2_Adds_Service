package com.sap.adds_service.adds.application.output;

import com.sap.adds_service.adds.domain.dto.PaidAddDTO;

public interface SendPaymentAddPort {
    void sendPaymentEvent(PaidAddDTO paidAddDTO);
}
