package com.sap.adds_service.adds.application.input;

import com.sap.adds_service.adds.domain.Add;

import java.time.LocalDateTime;
import java.util.UUID;

public interface GetRandomAddPort {
    Add randomAddByTypeAndCinemaId(String type, UUID cinemaId, LocalDateTime currentDateTime);
}
