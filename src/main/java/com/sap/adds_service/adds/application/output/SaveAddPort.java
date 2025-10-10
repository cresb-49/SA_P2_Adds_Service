package com.sap.adds_service.adds.application.output;

import com.sap.adds_service.adds.domain.Add;

public interface SaveAddPort {
    Add save(Add add);
}
