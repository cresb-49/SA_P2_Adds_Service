package com.sap.adds_service.adds.application.output;

import java.util.UUID;

public interface FindUserPort {
    boolean existsById(UUID userId);
}
