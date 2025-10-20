

package com.sap.adds_service.duration.application.usecases.findduration;

import com.sap.adds_service.duration.application.output.FindDurationPort;
import com.sap.adds_service.duration.domain.Duration;
import com.sap.common_lib.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindDurationCaseTest {

    @Mock private FindDurationPort findDurationPort;
    @InjectMocks private FindDurationCase useCase;

    private static final UUID ID = UUID.randomUUID();

    @Test
    void findById_shouldReturnDuration_whenExists() {
        // Arrange
        var duration = new Duration(ID, 7, LocalDateTime.now());
        when(findDurationPort.findById(ID)).thenReturn(Optional.of(duration));
        // Act
        var result = useCase.findById(ID);
        // Assert
        assertThat(result).isEqualTo(duration);
        verify(findDurationPort).findById(ID);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        // Arrange
        when(findDurationPort.findById(ID)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> useCase.findById(ID)).isInstanceOf(NotFoundException.class);
        verify(findDurationPort).findById(ID);
    }

    @Test
    void findAll_shouldReturnList() {
        // Arrange
        var d1 = new Duration(UUID.randomUUID(), 3, LocalDateTime.now());
        var d2 = new Duration(UUID.randomUUID(), 7, LocalDateTime.now());
        when(findDurationPort.findAll()).thenReturn(List.of(d1, d2));
        // Act
        var result = useCase.findAll();
        // Assert
        assertThat(result).containsExactly(d1, d2);
        verify(findDurationPort).findAll();
    }

    @Test
    void findByIds_shouldReturnList() {
        // Arrange
        var ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        var d1 = new Duration(ids.get(0), 3, LocalDateTime.now());
        var d2 = new Duration(ids.get(1), 5, LocalDateTime.now());
        when(findDurationPort.findByIds(ids)).thenReturn(List.of(d1, d2));
        // Act
        var result = useCase.findByIds(ids);
        // Assert
        assertThat(result).containsExactly(d1, d2);
        verify(findDurationPort).findByIds(ids);
    }
}