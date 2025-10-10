package com.sap.adds_service.adds.application.usecases.getrandomadd;

import com.sap.adds_service.adds.application.input.GetRandomAddPort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.domain.Add;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class GetRandomAddCase implements GetRandomAddPort {

    private final FindingAddPort findingAddPort;

    @Override
    public Add randomAddByTypeAndCinemaId(String type, UUID cinemaId) {
        return findingAddPort.findAddRandomByTypeAndCinemaId(type, cinemaId).orElseThrow(
                () -> new RuntimeException("No adds found for the given type and cinemaId")
        );
    }
}
