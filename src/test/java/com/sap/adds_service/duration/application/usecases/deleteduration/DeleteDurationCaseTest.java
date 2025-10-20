

package com.sap.adds_service.duration.application.usecases.deleteduration;

import com.sap.adds_service.duration.application.output.DeleteDurationPort;
import com.sap.adds_service.duration.application.output.FindDurationPort;
import com.sap.adds_service.duration.domain.Duration;
import com.sap.common_lib.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteDurationCaseTest {

    @Mock private DeleteDurationPort deleteDurationPort;
    @Mock private FindDurationPort findDurationPort;

    @InjectMocks private DeleteDurationCase useCase;

    private static final UUID DURATION_ID = UUID.randomUUID();

    @Test
    void deleteById_shouldDelete_whenDurationExists() {
        // Arrange
        var duration = new Duration(DURATION_ID, 7, LocalDateTime.now());
        when(findDurationPort.findById(DURATION_ID)).thenReturn(Optional.of(duration));
        // Act
        useCase.deleteById(DURATION_ID);
        // Assert
        verify(findDurationPort).findById(DURATION_ID);
        verify(deleteDurationPort).deleteById(DURATION_ID);
    }

    @Test
    void deleteById_shouldThrow_whenDurationNotFound() {
        // Arrange
        when(findDurationPort.findById(DURATION_ID)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> useCase.deleteById(DURATION_ID)).isInstanceOf(NotFoundException.class);
        verify(findDurationPort).findById(DURATION_ID);
        verifyNoInteractions(deleteDurationPort);
    }
}