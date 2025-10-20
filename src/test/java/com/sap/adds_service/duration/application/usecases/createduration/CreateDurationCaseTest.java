

package com.sap.adds_service.duration.application.usecases.createduration;

import com.sap.adds_service.duration.application.output.FindDurationPort;
import com.sap.adds_service.duration.application.output.SaveDurationPort;
import com.sap.adds_service.duration.application.usecases.createduration.dtos.CreateDurationDTO;
import com.sap.adds_service.duration.domain.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDurationCaseTest {

    @Mock
    private FindDurationPort findDurationPort;

    @Mock
    private SaveDurationPort saveDurationPort;

    @InjectMocks
    private CreateDurationCase createDurationCase;

    private static final int DAYS = 7;

    private CreateDurationDTO createDurationDTO;

    @BeforeEach
    void setUp() {
        createDurationDTO = new CreateDurationDTO(DAYS);
    }

    @Test
    void create_shouldThrow_whenDurationExists() {
        // Arrange
        when(findDurationPort.existsByDaysDuration(DAYS)).thenReturn(true);
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createDurationCase.create(createDurationDTO));
        verify(findDurationPort).existsByDaysDuration(DAYS);
        verifyNoInteractions(saveDurationPort);
    }

    @Test
    void create_shouldSave_whenDurationIsValid() {
        // Arrange
        when(findDurationPort.existsByDaysDuration(DAYS)).thenReturn(false);
        // Act
        Duration result = createDurationCase.create(createDurationDTO);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDays()).isEqualTo(DAYS);
        verify(findDurationPort).existsByDaysDuration(DAYS);
        verify(saveDurationPort).save(any(Duration.class));
    }
}