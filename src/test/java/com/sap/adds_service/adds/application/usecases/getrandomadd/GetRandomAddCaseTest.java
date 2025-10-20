package com.sap.adds_service.adds.application.usecases.getrandomadd;

import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.common_lib.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRandomAddCaseTest {

    @Mock
    private FindingAddPort findingAddPort;

    @InjectMocks
    private GetRandomAddCase useCase;

    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Test
    @DisplayName("randomAddByTypeAndCinemaId: retorna Add cuando existe uno disponible")
    void randomAddByTypeAndCinemaId_shouldReturn_whenFound() {
        // Arrange
        Add expectedAdd = new Add(
                UUID.randomUUID(),
                "Contenido",
                AddType.TEXT_BANNER,
                "text/plain",
                false,
                null,
                true,
                "Descripción",
                CINEMA_ID,
                UUID.randomUUID(),
                PaymentState.COMPLETED,
                NOW,
                BigDecimal.valueOf(15),
                NOW.plusDays(5),
                NOW,
                NOW
        );
        when(findingAddPort.findAddRandomByTypeAndCinemaIdAndNow(
                AddType.TEXT_BANNER.toString(),
                CINEMA_ID,
                PaymentState.COMPLETED.toString(),
                NOW
        )).thenReturn(Optional.of(expectedAdd));

        // Act
        Add result = useCase.randomAddByTypeAndCinemaId(AddType.TEXT_BANNER, CINEMA_ID, NOW);

        // Assert
        assertThat(result).isEqualTo(expectedAdd);
        verify(findingAddPort, times(1))
                .findAddRandomByTypeAndCinemaIdAndNow(AddType.TEXT_BANNER.toString(), CINEMA_ID, PaymentState.COMPLETED.toString(), NOW);
    }

    @Test
    @DisplayName("randomAddByTypeAndCinemaId: lanza NotFoundException cuando no hay anuncios disponibles")
    void randomAddByTypeAndCinemaId_shouldThrow_whenNotFound() {
        // Arrange
        when(findingAddPort.findAddRandomByTypeAndCinemaIdAndNow(
                AddType.MEDIA_HORIZONTAL.toString(),
                CINEMA_ID,
                PaymentState.COMPLETED.toString(),
                NOW
        )).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.randomAddByTypeAndCinemaId(AddType.MEDIA_HORIZONTAL, CINEMA_ID, NOW))
                .isInstanceOf(NotFoundException.class);
        verify(findingAddPort, times(1))
                .findAddRandomByTypeAndCinemaIdAndNow(AddType.MEDIA_HORIZONTAL.toString(), CINEMA_ID, PaymentState.COMPLETED.toString(), NOW);
    }
}
