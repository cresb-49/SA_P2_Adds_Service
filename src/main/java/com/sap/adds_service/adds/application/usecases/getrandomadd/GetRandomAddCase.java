package com.sap.adds_service.adds.application.usecases.getrandomadd;

import com.sap.adds_service.adds.application.input.GetRandomAddPort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.common_lib.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class GetRandomAddCase implements GetRandomAddPort {

    private final FindingAddPort findingAddPort;

    @Override
    public Add randomAddByTypeAndCinemaId(AddType type, UUID cinemaId, LocalDateTime currentDateTime) {

        return findingAddPort.findAddRandomByTypeAndCinemaIdAndNow(type.toString(), cinemaId, PaymentState.COMPLETED.toString(), currentDateTime).orElseThrow(
                () -> new NotFoundException("No adds found for the given type and cinemaId")
        );

    }
}
