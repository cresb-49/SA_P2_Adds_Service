package com.sap.adds_service.adds.application.input;

import com.sap.adds_service.adds.domain.Add;

import java.util.UUID;

public interface ChangeStateAddPort {
    Add changeState(UUID id);
}
