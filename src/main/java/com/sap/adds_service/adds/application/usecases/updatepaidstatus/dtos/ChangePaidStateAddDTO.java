package com.sap.adds_service.adds.application.usecases.updatepaidstatus.dtos;

import java.util.UUID;

public record ChangePaidStateAddDTO(
        UUID addId, // UUID of the add
        boolean paid, // new paid state of the add
        String message // additional message why the state was changed
) {
}
