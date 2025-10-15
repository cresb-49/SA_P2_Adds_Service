package com.sap.adds_service.duration.infrastructure.input.domain.gateway;

import com.sap.adds_service.duration.application.output.FindDurationPort;
import com.sap.adds_service.duration.domain.Duration;
import com.sap.adds_service.duration.infrastructure.input.domain.port.DurationGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Transactional(readOnly = true)
public class DurationGateway implements DurationGatewayPort {

    private final FindDurationPort findDurationPort;

    @Override
    public Optional<Duration> findById(UUID id) {
        return findDurationPort.findById(id);
    }
}
