package com.sap.adds_service.adds.application.input;

import com.sap.adds_service.adds.domain.dtos.GananciasAnuncianteReportDTO;

import java.time.LocalDate;
import java.util.UUID;

public interface ReporteGananciasAnuncianteCasePort {
    GananciasAnuncianteReportDTO reporteGananciasAnunciante(LocalDate from, LocalDate to, UUID userId);

    byte[] generarReporteGananciasAnunciante(LocalDate from, LocalDate to, UUID userId);
}
