package com.sap.adds_service.adds.application.input;

import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;

import java.time.LocalDateTime;
import java.util.UUID;

public interface GetRandomAddPort {
    Add randomAddByTypeAndCinemaId(AddType type, UUID cinemaId, LocalDateTime currentDateTime);
}
