package com.sap.adds_service.adds.application.output;

import java.util.UUID;

public interface SendNotificationPort {
    void sendNotification(UUID userId, String message);
}
