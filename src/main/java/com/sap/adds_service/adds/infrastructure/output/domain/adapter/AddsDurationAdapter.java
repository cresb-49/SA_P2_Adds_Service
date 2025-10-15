package com.sap.adds_service.adds.infrastructure.output.domain.adapter;

import com.sap.adds_service.adds.application.output.FindDurationPort;
import com.sap.adds_service.adds.domain.DurationView;
import com.sap.adds_service.adds.infrastructure.output.domain.mapper.DurationViewMapper;
import com.sap.adds_service.duration.infrastructure.input.domain.port.DurationGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class AddsDurationAdapter implements FindDurationPort {

    private final DurationGatewayPort durationGatewayPort;
    private final DurationViewMapper durationViewMapper;

    @Override
    public Optional<DurationView> findById(UUID id) {
        return durationGatewayPort.findById(id).map(durationViewMapper::toDomain);
    }
}
