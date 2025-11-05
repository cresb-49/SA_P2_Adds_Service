package com.sap.adds_service.adds.application.usecases.reportbuyadds;

import com.sap.adds_service.adds.application.factory.AddFactory;
import com.sap.adds_service.adds.application.output.FindPurchasedAdds;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.adds_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyAddsReportCaseTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2024, 1, 10, 0, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2024, 1, 15, 23, 59);
    private static final LocalDate PERIOD_FROM = LocalDate.of(2024, 1, 1);
    private static final LocalDate PERIOD_TO = LocalDate.of(2024, 1, 31);

    @Mock
    private FindPurchasedAdds findPurchasedAdds;

    @Mock
    private JasperReportServicePort jasperReportService;

    @Mock
    private AddFactory addFactory;

    @InjectMocks
    private BuyAddsReportCase useCase;

    @Test
    void report_shouldFetchPurchasedAddsAndEnrich() {
        var rawAdds = new ArrayList<>(List.of(sampleAdd()));
        var enrichedAdds = new ArrayList<>(List.of(sampleAdd()));

        when(findPurchasedAdds.findPurchasedAdds(FROM, TO, "MEDIA_VERTICAL", PERIOD_FROM, PERIOD_TO, PaymentState.COMPLETED.name()))
                .thenReturn(rawAdds);
        when(addFactory.withCinemaAndUser(rawAdds)).thenReturn(enrichedAdds);

        var result = useCase.report(FROM, TO, "MEDIA_VERTICAL", PERIOD_FROM, PERIOD_TO);

        assertThat(result).isSameAs(enrichedAdds);
        verify(addFactory).withCinemaAndUser(rawAdds);
    }

    @Test
    void generateReportFile_shouldInvokeJasperWithTemplateAndParams() {
        var rawAdds = new ArrayList<>(List.of(sampleAdd()));
        when(findPurchasedAdds.findPurchasedAdds(any(), any(), any(), any(), any(), any()))
                .thenReturn(rawAdds);
        when(addFactory.withCinemaAndUser(rawAdds)).thenReturn(rawAdds);
        byte[] pdf = "bytes".getBytes();
        when(jasperReportService.toPdf(any(), any(), any())).thenReturn(pdf);

        var result = useCase.generateReportFile(FROM, TO, "MEDIA_VERTICAL", PERIOD_FROM, PERIOD_TO);

        assertThat(result).isEqualTo(pdf);

        ArgumentCaptor<String> templateCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Add>> dataCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<HashMap<String, Object>> paramsCaptor = ArgumentCaptor.forClass(HashMap.class);
        verify(jasperReportService).toPdf(templateCaptor.capture(), dataCaptor.capture(), paramsCaptor.capture());

        assertThat(templateCaptor.getValue()).isEqualTo("ads_purchased_report");
        assertThat(dataCaptor.getValue()).isSameAs(rawAdds);
        var params = paramsCaptor.getValue();
        assertThat(params.get("reportTitle")).isEqualTo("Anuncios Comprados");
        assertThat(params.get("from")).isEqualTo(FROM);
        assertThat(params.get("to")).isEqualTo(TO);
        assertThat(params.get("addType")).isEqualTo("MEDIA_VERTICAL");
        assertThat(params.get("periodFrom")).isEqualTo(PERIOD_FROM);
        assertThat(params.get("periodTo")).isEqualTo(PERIOD_TO);
    }

    private Add sampleAdd() {
        return new Add(
                UUID.randomUUID(),
                "content",
                AddType.MEDIA_VERTICAL,
                "image/png",
                false,
                "http://content",
                true,
                "description",
                UUID.randomUUID(),
                UUID.randomUUID(),
                PaymentState.COMPLETED,
                LocalDateTime.now(),
                BigDecimal.TEN,
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(2)
        );
    }
}
