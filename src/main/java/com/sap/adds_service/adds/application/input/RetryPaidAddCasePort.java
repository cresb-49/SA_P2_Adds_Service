package com.sap.adds_service.adds.application.input;

import java.util.UUID;

public interface RetryPaidAddCasePort {
    void retryPaidAdd(UUID addId);
}
