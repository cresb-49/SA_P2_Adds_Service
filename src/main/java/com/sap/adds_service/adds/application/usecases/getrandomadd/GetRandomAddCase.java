package com.sap.adds_service.adds.application.usecases.getrandomadd;

import com.sap.adds_service.adds.application.input.GetRandomAddPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class GetRandomAddCase implements GetRandomAddPort {
}
