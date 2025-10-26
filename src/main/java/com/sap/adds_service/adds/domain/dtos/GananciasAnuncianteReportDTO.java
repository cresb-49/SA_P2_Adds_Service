package com.sap.adds_service.adds.domain.dtos;

import com.sap.adds_service.adds.domain.Add;

import java.math.BigDecimal;
import java.util.List;

public record GananciasAnuncianteReportDTO(
        List<Add> adds,
        BigDecimal totalGanancias
) {
}
