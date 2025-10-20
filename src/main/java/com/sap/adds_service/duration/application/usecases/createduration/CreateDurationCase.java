package com.sap.adds_service.duration.application.usecases.createduration;

import com.sap.adds_service.duration.application.input.CreateDurationCasePort;
import com.sap.adds_service.duration.application.output.FindDurationPort;
import com.sap.adds_service.duration.application.output.SaveDurationPort;
import com.sap.adds_service.duration.application.usecases.createduration.dtos.CreateDurationDTO;
import com.sap.adds_service.duration.domain.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CreateDurationCase implements CreateDurationCasePort {

    private final FindDurationPort findDurationPort;
    private final SaveDurationPort saveDurationPort;

    @Override
    public Duration create(CreateDurationDTO createDurationDTO) {
        // Check if a duration with the same name already exists
        if (findDurationPort.existsByDaysDuration(createDurationDTO.days())) {
            throw new IllegalArgumentException("Ya existe una duración con " + createDurationDTO.days() + " días");
        }
        // Create a new duration
        var duration = new Duration(createDurationDTO.days());
        // Validate the duration
        duration.validate();
        // Save the duration
        saveDurationPort.save(duration);
        // Return the saved duration
        return duration;
    }
}
