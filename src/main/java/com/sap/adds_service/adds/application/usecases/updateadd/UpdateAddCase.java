package com.sap.adds_service.adds.application.usecases.updateadd;

import com.sap.adds_service.adds.application.input.UpdateAddPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UpdateAddCase implements UpdateAddPort {
}
