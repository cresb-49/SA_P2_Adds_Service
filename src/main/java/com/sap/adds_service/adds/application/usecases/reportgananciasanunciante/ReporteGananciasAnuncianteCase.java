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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReporteGananciasAnuncianteCase implements ReporteGananciasAnuncianteCasePort {

    private static final String REPORT_TEMPLATE = "ads_purchased_report_user";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

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
            params.put("userId", userId);
            params.put("userFullName", reporte.adds().getFirst().getUserFullName());
        }
        var flatData = toFlatRows(reporte.adds());
        return jasperReportService.toPdfCompiled(
                REPORT_TEMPLATE,
                flatData,
                params
        );
    }

    private List<Map<String, Object>> toFlatRows(List<AddGananciasAnuncianteReportLineDTO> adds) {
        return adds.stream()
                .map(this::toFlatRow)
                .toList();
    }

    private Map<String, Object> toFlatRow(AddGananciasAnuncianteReportLineDTO add) {
        var row = new HashMap<String, Object>();
        row.put("id", add.getId() == null ? "" : add.getId().toString());
        row.put("type", add.getType() == null ? "" : add.getType().name());
        row.put("paidAt", formatDate(add.getPaidAt()));
        row.put("price", add.getPrice());
        row.put("addExpiration", formatDate(add.getAddExpiration()));
        row.put("userFullName", safeString(add.getUserFullName()));
        return row;
    }

    private static String safeString(String value) {
        return value == null ? "" : value;
    }

    private static String formatDate(LocalDateTime value) {
        return value == null ? "" : value.format(DATE_TIME_FORMATTER);
    }
}
