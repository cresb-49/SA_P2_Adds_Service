package com.sap.adds_service.duration.application.usecases.deleteduration;

import com.sap.adds_service.duration.application.input.DeleteDurationCasePort;
import com.sap.adds_service.duration.application.output.DeleteDurationPort;
import com.sap.adds_service.duration.application.output.FindDurationPort;
import com.sap.common_lib.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DeleteDurationCase implements DeleteDurationCasePort {

    private final DeleteDurationPort deleteDurationPort;
    private final FindDurationPort findDurationPort;

    @Override
    public void deleteById(UUID id) {
        // Check if the duration exists
        findDurationPort.findById(id).orElseThrow(
                () -> new NotFoundException("Duración no encontrada")
        );
        // Delete duration
        deleteDurationPort.deleteById(id);
    }
}
