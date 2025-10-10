package com.sap.adds_service.adds.application.usecases.changestatecase;

import com.sap.adds_service.adds.application.input.ChangeStateAddPort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.domain.Add;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ChangeStateAddCase implements ChangeStateAddPort {

    private final FindingAddPort findingAddPort;
    private final SaveAddPort saveAddPort;

    @Override
    public Add changeState(UUID id) {
        Add add = findingAddPort.findById(id).orElseThrow(
                () -> new RuntimeException("Add not found")
        );
        // Toggle the active state
        add.changeActive();
        // Save the updated add
        return saveAddPort.save(add);
    }
}
