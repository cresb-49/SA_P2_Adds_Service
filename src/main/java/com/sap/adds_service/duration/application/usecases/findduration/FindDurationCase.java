package com.sap.adds_service.duration.application.usecases.findduration;

import com.sap.adds_service.duration.application.input.FindDurationCasePort;
import com.sap.adds_service.duration.application.output.FindDurationPort;
import com.sap.adds_service.duration.domain.Duration;
import com.sap.common_lib.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class FindDurationCase implements FindDurationCasePort {

    private final FindDurationPort findDurationPort;

    @Override
    public Duration findById(UUID id) {
        return findDurationPort.findById(id).orElseThrow(
                () -> new NotFoundException("Duración no encontrada")
        );
    }

    @Override
    public List<Duration> findAll() {
        return findDurationPort.findAll();
    }

    @Override
    public List<Duration> findByIds(List<UUID> ids) {
        return findDurationPort.findByIds(ids);
    }
}
