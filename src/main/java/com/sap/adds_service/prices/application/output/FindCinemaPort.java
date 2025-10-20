package com.sap.adds_service.prices.application.output;

import java.util.UUID;

public interface FindCinemaPort {
    boolean checkIfCinemaExistsById(UUID id);
}
