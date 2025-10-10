package com.sap.adds_service.adds.application.input;

import com.sap.adds_service.adds.application.usecases.updateadd.dtos.UpdateAddDTO;
import com.sap.adds_service.adds.domain.Add;

public interface UpdateAddPort {
    Add update(UpdateAddDTO updateAddDTO);
}
