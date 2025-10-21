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
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BuyAddsReportCase implements BuyAddsReportCasePort {

    private static final String REPORT_TITLE = "Anuncios Comprados";
    private static final String REPORT_TEMPLATE = "ads_purchased_report";

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
            LocalDate periodTo,
            String fileType
    ) {
        var data = report(from, to, addType, periodFrom, periodTo);
        var params = new HashMap<String, Object>();
        params.put("reportTitle", REPORT_TITLE);
        params.put("from", from);
        params.put("to", to);
        params.put("addType", addType);
        params.put("periodFrom", periodFrom);
        params.put("periodTo", periodTo);
        return jasperReportService.toPdf(REPORT_TEMPLATE, data, params);
    }
}
