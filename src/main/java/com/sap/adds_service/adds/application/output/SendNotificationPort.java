package com.sap.adds_service.adds.application.output;

import com.sap.adds_service.adds.domain.dto.NotificacionDTO;

public interface SendNotificationPort {
    void sendNotification(NotificacionDTO notificacionDTO);
}
