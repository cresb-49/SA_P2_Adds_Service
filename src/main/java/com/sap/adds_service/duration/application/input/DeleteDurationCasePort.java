package com.sap.adds_service.duration.application.input;

import java.util.UUID;

public interface DeleteDurationCasePort {
    void deleteById(UUID id);
}
