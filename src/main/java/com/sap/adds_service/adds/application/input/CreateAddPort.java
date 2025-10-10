package com.sap.adds_service.adds.application.input;

import com.sap.adds_service.adds.application.usecases.createadd.dtos.CreateAddDTO;
import com.sap.adds_service.adds.domain.Add;

public interface CreateAddPort {
    Add create(CreateAddDTO createAddDTO);
}
