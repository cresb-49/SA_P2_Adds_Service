package com.sap.adds_service.adds.application.output;

import java.math.BigDecimal;
import java.util.UUID;

public interface SendNotificationAddPayment {
    void sendPaymentEvent(UUID addId, UUID userId, BigDecimal amount);
}
