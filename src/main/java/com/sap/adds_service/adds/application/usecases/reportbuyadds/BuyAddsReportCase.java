package com.sap.adds_service.adds.application.usecases.reportbuyadds;

import com.sap.adds_service.adds.application.factory.AddFactory;
import com.sap.adds_service.adds.application.input.BuyAddsReportCasePort;
import com.sap.adds_service.adds.application.output.FindPurchasedAdds;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.adds_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BuyAddsReportCase implements BuyAddsReportCasePort {

    private static final String REPORT_TITLE = "Anuncios Comprados";
    private static final String REPORT_TEMPLATE = "ads_purchased_report";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final FindPurchasedAdds findPurchasedAdds;
    private final JasperReportServicePort jasperReportService;
    private final AddFactory addFactory;

    @Override
    public List<Add> report(
            LocalDateTime from,
            LocalDateTime to,
            String addType,
            LocalDate periodFrom,
            LocalDate periodTo
    ) {
        var adds = findPurchasedAdds.findPurchasedAdds(
                from,
                to,
                addType,
                periodFrom,
                periodTo,
                PaymentState.COMPLETED.name()
        );
        return addFactory.withCinemaAndUser(adds);
    }

    @Override
    public byte[] generateReportFile(
            LocalDateTime from,
            LocalDateTime to,
            String addType,
            LocalDate periodFrom,
            LocalDate periodTo
    ) {
        var data = report(from, to, addType, periodFrom, periodTo);
        var params = new HashMap<String, Object>();
        params.put("reportTitle", REPORT_TITLE);
        params.put("from", from);
        params.put("to", to);
        params.put("addType", addType);
        params.put("periodFrom", periodFrom);
        params.put("periodTo", periodTo);
        var flatData = toFlatRows(data);
        return jasperReportService.toPdfCompiled(REPORT_TEMPLATE, flatData, params);
    }

    private List<Map<String, Object>> toFlatRows(List<Add> adds) {
        return adds.stream()
                .map(this::toFlatRow)
                .toList();
    }

    private Map<String, Object> toFlatRow(Add add) {
        var row = new HashMap<String, Object>();
        row.put("id", add.getId() == null ? "" : add.getId().toString());
        row.put("type", add.getType() == null ? "" : add.getType().name());
        row.put("price", add.getPrice());
        row.put("paidAt", formatDate(add.getPaidAt()));
        row.put("addExpiration", formatDate(add.getAddExpiration()));
        row.put("content", safeString(add.getContent()));
        row.put("description", safeString(add.getDescription()));
        row.put("cinemaName", safeString(add.getCinemaName()));
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
