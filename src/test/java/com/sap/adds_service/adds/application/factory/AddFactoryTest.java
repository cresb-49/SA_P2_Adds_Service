package com.sap.adds_service.adds.application.factory;

import com.sap.adds_service.adds.application.output.FindCinemaPort;
import com.sap.adds_service.adds.application.output.FindUserPort;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.adds_service.adds.domain.dtos.CinemaView;
import com.sap.adds_service.adds.domain.dtos.UserView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddFactoryTest {

    private static final UUID CINEMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID ANOTHER_CINEMA_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID USER_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final UUID ANOTHER_USER_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    @Mock
    private FindCinemaPort findCinemaPort;

    @Mock
    private FindUserPort findUserPort;

    @InjectMocks
    private AddFactory addFactory;

    @Test
    void withCinemaAndUser_shouldEnrichSingleAdd() {
        var add = sampleAdd(CINEMA_ID, USER_ID);
        var cinemaView = new CinemaView(CINEMA_ID, "Cinema One");
        var userView = new UserView(USER_ID, "Jane", "Doe", "jane@example.com");
        when(findCinemaPort.findById(CINEMA_ID)).thenReturn(cinemaView);
        when(findUserPort.findById(USER_ID)).thenReturn(userView);

        var result = addFactory.withCinemaAndUser(add);

        assertThat(result.getCinema()).isEqualTo(cinemaView);
        assertThat(result.getUser()).isEqualTo(userView);
    }

    @Test
    void withCinemaAndUser_shouldBatchEnrichAdds() {
        var firstAdd = sampleAdd(CINEMA_ID, USER_ID);
        var secondAdd = sampleAdd(ANOTHER_CINEMA_ID, ANOTHER_USER_ID);
        var adds = new ArrayList<>(List.of(firstAdd, secondAdd));

        var cinemas = List.of(
                new CinemaView(CINEMA_ID, "Cinema One"),
                new CinemaView(ANOTHER_CINEMA_ID, "Cinema Two")
        );
        var users = List.of(
                new UserView(USER_ID, "Jane", "Doe", "jane@example.com"),
                new UserView(ANOTHER_USER_ID, "John", "Smith", "john@example.com")
        );
        when(findCinemaPort.findByIds(anyList())).thenReturn(cinemas);
        when(findUserPort.findByIds(anyList())).thenReturn(users);

        var result = addFactory.withCinemaAndUser(adds);

        assertThat(result).containsExactly(firstAdd, secondAdd);
        assertThat(firstAdd.getCinema().name()).isEqualTo("Cinema One");
        assertThat(secondAdd.getUser().firstName()).isEqualTo("John");
        verify(findCinemaPort).findByIds(List.of(CINEMA_ID, ANOTHER_CINEMA_ID));
        verify(findUserPort).findByIds(List.of(USER_ID, ANOTHER_USER_ID));
    }

    @Test
    void withUser_shouldEnrichSingleAdd() {
        var add = sampleAdd(CINEMA_ID, USER_ID);
        var userView = new UserView(USER_ID, "Jane", "Doe", "jane@example.com");
        when(findUserPort.findById(USER_ID)).thenReturn(userView);

        var result = addFactory.withUser(add);

        assertThat(result.getUser()).isEqualTo(userView);
    }

    @Test
    void withUser_shouldBatchEnrichAdds() {
        var firstAdd = sampleAdd(CINEMA_ID, USER_ID);
        var secondAdd = sampleAdd(ANOTHER_CINEMA_ID, ANOTHER_USER_ID);
        var adds = new ArrayList<>(List.of(firstAdd, secondAdd));

        var users = List.of(
                new UserView(USER_ID, "Jane", "Doe", "jane@example.com"),
                new UserView(ANOTHER_USER_ID, "John", "Smith", "john@example.com")
        );
        when(findUserPort.findByIds(anyList())).thenReturn(users);

        var result = addFactory.withUser(adds);

        assertThat(result).containsExactly(firstAdd, secondAdd);
        assertThat(firstAdd.getUser().firstName()).isEqualTo("Jane");
        assertThat(secondAdd.getUser().firstName()).isEqualTo("John");
        verify(findUserPort).findByIds(List.of(USER_ID, ANOTHER_USER_ID));
    }

    private Add sampleAdd(UUID cinemaId, UUID userId) {
        return new Add(
                UUID.randomUUID(),
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
                LocalDateTime.now(),
                BigDecimal.TEN,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1)
        );
    }
}
