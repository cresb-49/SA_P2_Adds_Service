package com.sap.adds_service.duration.application.output;

import com.sap.adds_service.duration.domain.Duration;

public interface SaveDurationPort {
    Duration save(Duration duration);
}
