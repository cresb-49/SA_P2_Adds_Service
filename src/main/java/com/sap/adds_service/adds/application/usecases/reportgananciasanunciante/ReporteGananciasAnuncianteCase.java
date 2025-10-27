package com.sap.adds_service.adds.application.usecases.reportgananciasanunciante;

import com.sap.adds_service.adds.application.factory.AddFactory;
import com.sap.adds_service.adds.application.input.ReporteGananciasAnuncianteCasePort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddFilter;
import com.sap.adds_service.adds.domain.dtos.AddGananciasAnuncianteReportLineDTO;
import com.sap.adds_service.adds.domain.dtos.GananciasAnuncianteReportDTO;
import com.sap.adds_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReporteGananciasAnuncianteCase implements ReporteGananciasAnuncianteCasePort {

    private static final String REPORT_TEMPLATE = "ads_purchased_report_user";

    private final AddFactory addFactory;
    private final JasperReportServicePort jasperReportService;
    private final FindingAddPort findingAddPort;

    @Override
    public GananciasAnuncianteReportDTO reporteGananciasAnunciante(LocalDate from, LocalDate to, UUID userId) {
        // Las fechas son datos obligatorios para el reporte, el usuario es opcional
        if (from == null || to == null) {
            throw new IllegalArgumentException("Las fechas 'from' y 'to' son obligatorias para generar el reporte de ganancias del anunciante.");
        }
        //Se recibe la fecha en formato LocalDate y se convierte a LocalDateTime para buscar los anuncios pagados en ese rango
        // La fecha minima se inicia a las 00:00:00 del dia y la fecha maxima a las 23:59:59 del dia
        LocalDateTime maxDateTime = to.atTime(23, 59, 59);
        LocalDateTime minDateTime = from.atTime(0, 0, 0);
        // Se crea un AddFilter con los parametros recibidos
        var filter = AddFilter.builder()
                .maxPaymentDate(maxDateTime)
                .minPaymentDate(minDateTime)
                .userId(userId)
                .build();
        // Se buscan los anuncios que cumplen con los filtros
        var adds = findingAddPort.findByFilers(filter);
        // Construir los anuncios con la information de los usuarios
        var addsWithUser = addFactory.withUser(adds);
        // Calcular las ganancias totales
        BigDecimal ganancias = addsWithUser.stream()
                .map(Add::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Retornar el DTO con los anuncios y las ganancias
        var linesReport = addsWithUser.stream()
                .map(add -> new AddGananciasAnuncianteReportLineDTO(add))
                .toList();
        return new GananciasAnuncianteReportDTO(linesReport, ganancias);
    }

    @Override
    public byte[] generarReporteGananciasAnunciante(LocalDate from, LocalDate to, UUID userId) {
        //Obtenemos el objeto del reporte
        var reporte = reporteGananciasAnunciante(from, to, userId);
        var params = new HashMap<String, Object>();
        params.put("from", from.atStartOfDay());
        params.put("to", to.atTime(23, 59, 59));
        if (userId != null && !reporte.adds().isEmpty()) {
            var firstAdd = reporte.adds().getFirst();
            params.put("userId", firstAdd.getId());
            params.put("userFullName", firstAdd.getUserFullName());
        }
        return jasperReportService.toPdf(
                REPORT_TEMPLATE,
                reporte.adds(),
                params
        );
    }
}
