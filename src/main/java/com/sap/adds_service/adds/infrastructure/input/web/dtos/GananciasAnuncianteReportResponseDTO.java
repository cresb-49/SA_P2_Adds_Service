package com.sap.adds_service.adds.infrastructure.input.web.dtos;

import com.sap.adds_service.adds.domain.dtos.AddGananciasAnuncianteReportLineDTO;
import com.sap.adds_service.adds.domain.dtos.GananciasAnuncianteReportDTO;

import java.math.BigDecimal;
import java.util.List;

public record GananciasAnuncianteReportResponseDTO(
        List<AddGananciasAnuncianteReportLineDTO> adds,
        BigDecimal totalGanancias
) {
    public static GananciasAnuncianteReportResponseDTO fromDomain(GananciasAnuncianteReportDTO domainDTO) {
        return new GananciasAnuncianteReportResponseDTO(
                domainDTO.adds(),
                domainDTO.totalGanancias()
        );
    }
}
