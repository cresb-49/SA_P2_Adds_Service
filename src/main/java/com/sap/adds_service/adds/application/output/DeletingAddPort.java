package com.sap.adds_service.adds.application.output;

import java.util.UUID;

public interface DeletingAddPort {
    void deleteById(UUID id);
}
