package com.sap.adds_service.adds.application.usecases.reportgananciasanunciante;

import com.sap.adds_service.adds.application.factory.AddFactory;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.adds_service.adds.domain.dtos.UserView;
import com.sap.adds_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteGananciasAnuncianteCaseTest {

    private static final LocalDate FROM = LocalDate.of(2024, 1, 10);
    private static final LocalDate TO = LocalDate.of(2024, 1, 15);
    private static final UUID USER_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID OTHER_USER_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Mock
    private AddFactory addFactory;

    @Mock
    private JasperReportServicePort jasperReportService;

    @Mock
    private FindingAddPort findingAddPort;

    @InjectMocks
    private ReporteGananciasAnuncianteCase useCase;

    private Add firstAdd;
    private Add secondAdd;
    private List<Add> adds;
    private Map<UUID, UserView> userViews;

    @BeforeEach
    void setUp() {
        firstAdd = buildAdd(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                USER_ID,
                BigDecimal.valueOf(150.50),
                FROM.atTime(10, 0)
        );
        secondAdd = buildAdd(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                UUID.fromString("44444444-4444-4444-4444-444444444444"),
                OTHER_USER_ID,
                BigDecimal.valueOf(49.99),
                FROM.atTime(12, 0)
        );
        adds = new ArrayList<>(List.of(firstAdd, secondAdd));

        userViews = Map.of(
                USER_ID, new UserView(USER_ID, "Jane", "Doe", "jane@example.com"),
                OTHER_USER_ID, new UserView(OTHER_USER_ID, "John", "Smith", "john@example.com")
        );
    }

    @Test
    void reporteGananciasAnunciante_shouldThrowWhenDatesMissing() {
        assertThatThrownBy(() -> useCase.reporteGananciasAnunciante(null, TO, USER_ID))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.reporteGananciasAnunciante(FROM, null, USER_ID))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reporteGananciasAnunciante_shouldAggregateTotalsAndBuildLines() {
        stubAddsWithUsers();
        var report = useCase.reporteGananciasAnunciante(FROM, TO, USER_ID);

        ArgumentCaptor<com.sap.adds_service.adds.domain.AddFilter> filterCaptor =
                ArgumentCaptor.forClass(com.sap.adds_service.adds.domain.AddFilter.class);
        verify(findingAddPort).findByFilers(filterCaptor.capture());
        var filter = filterCaptor.getValue();
        assertThat(filter.minPaymentDate()).isEqualTo(FROM.atStartOfDay());
        assertThat(filter.maxPaymentDate()).isEqualTo(TO.atTime(23, 59, 59));
        assertThat(filter.userId()).isEqualTo(USER_ID);

        assertThat(report.totalGanancias()).isEqualTo(firstAdd.getPrice().add(secondAdd.getPrice()));
        assertThat(report.adds()).hasSize(2);
        assertThat(report.adds().getFirst().getId()).isEqualTo(firstAdd.getId());
        assertThat(report.adds().getFirst().getUserFullName()).isEqualTo("Jane Doe");
    }

    @Test
    void generarReporteGananciasAnunciante_shouldSendReportDataToJasper() {
        stubAddsWithUsers();
        byte[] expectedPdf = "pdf".getBytes();
        when(jasperReportService.toPdf(any(), any(), any())).thenReturn(expectedPdf);

        var result = useCase.generarReporteGananciasAnunciante(FROM, TO, USER_ID);

        assertThat(result).isEqualTo(expectedPdf);
        ArgumentCaptor<String> templateCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Map<String, Object>>> dataCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

        verify(jasperReportService).toPdf(templateCaptor.capture(), dataCaptor.capture(), paramsCaptor.capture());

        assertThat(templateCaptor.getValue()).isEqualTo("ads_purchased_report_user");

        var params = paramsCaptor.getValue();
        assertThat(params.get("from")).isEqualTo(FROM.atStartOfDay());
        assertThat(params.get("to")).isEqualTo(TO.atTime(23, 59, 59));
        assertThat(params.get("userId")).isEqualTo(USER_ID);
        assertThat(params.get("userFullName")).isEqualTo("Jane Doe");

        var lines = dataCaptor.getValue();
        assertThat(lines).hasSize(2);
        var firstRow = lines.getFirst();
        assertThat(firstRow.get("id")).isEqualTo(firstAdd.getId().toString());
        assertThat(firstRow.get("type")).isEqualTo("MEDIA_VERTICAL");
        assertThat(firstRow.get("userFullName")).isEqualTo("Jane Doe");
        assertThat(firstRow.get("paidAt")).isEqualTo("10-01-2024 10:00");
    }

    private Add buildAdd(UUID id, UUID cinemaId, UUID userId, BigDecimal price, LocalDateTime paidAt) {
        return new Add(
                id,
                "content",
                AddType.MEDIA_VERTICAL,
                "image/png",
                false,
                "http://content",
                true,
                "description",
                cinemaId,
                userId,
                PaymentState.COMPLETED,
                paidAt,
                price,
                paidAt.plusDays(7),
                paidAt.minusDays(5),
                paidAt.minusDays(3)
        );
    }

    @SuppressWarnings("unchecked")
    private void stubAddsWithUsers() {
        when(findingAddPort.findByFilers(any())).thenReturn(adds);
        when(addFactory.withUser(org.mockito.ArgumentMatchers.<List<Add>>any())).thenAnswer(invocation -> {
            List<Add> result = (List<Add>) invocation.getArgument(0);
            result.forEach(add -> add.setUser(userViews.get(add.getUserId())));
            return result;
        });
    }
}
