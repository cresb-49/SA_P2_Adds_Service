package com.sap.adds_service.adds.application.input;

import com.sap.adds_service.adds.domain.Add;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BuyAddsReportCasePort {
    List<Add> report(
            LocalDateTime from,
            LocalDateTime to,
            String addType,
            LocalDate periodFrom,
            LocalDate periodTo
    );

    byte[] generateReportFile(
            LocalDateTime from,
            LocalDateTime to,
            String addType,
            LocalDate periodFrom,
            LocalDate periodTo,
            String fileType
    );
}
