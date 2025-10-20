

package com.sap.adds_service.adds.infrastructure.output.domain.adapter;

import com.sap.adds_service.adds.domain.DurationView;
import com.sap.adds_service.adds.infrastructure.output.domain.mapper.DurationViewMapper;
import com.sap.adds_service.duration.domain.Duration;
import com.sap.adds_service.duration.infrastructure.input.domain.port.DurationGatewayPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddsDurationAdapterTest {

    @Mock
    private DurationGatewayPort durationGatewayPort;

    @Mock
    private DurationViewMapper durationViewMapper;

    @InjectMocks
    private AddsDurationAdapter adapter;

    private static final UUID ID = UUID.randomUUID();

    private Duration duration;
    private DurationView durationView;

    @BeforeEach
    void setup() {
        duration = new Duration(ID, 7, LocalDateTime.now());
        durationView = new DurationView(ID, 7, LocalDateTime.now());
    }

    @Test
    void findById_shouldReturnMappedDurationView_whenDurationExists() {
        // Arrange
        when(durationGatewayPort.findById(ID)).thenReturn(Optional.of(duration));
        when(durationViewMapper.toDomain(duration)).thenReturn(durationView);

        // Act
        Optional<DurationView> result = adapter.findById(ID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(durationView);
    }

    @Test
    void findById_shouldReturnEmpty_whenDurationNotExists() {
        // Arrange
        when(durationGatewayPort.findById(ID)).thenReturn(Optional.empty());

        // Act
        Optional<DurationView> result = adapter.findById(ID);

        // Assert
        assertThat(result).isEmpty();
    }
}