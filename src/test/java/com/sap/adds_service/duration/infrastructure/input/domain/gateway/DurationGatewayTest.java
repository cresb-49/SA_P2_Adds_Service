

package com.sap.adds_service.duration.infrastructure.input.domain.gateway;

import com.sap.adds_service.duration.application.output.FindDurationPort;
import com.sap.adds_service.duration.domain.Duration;
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
class DurationGatewayTest {

    @Mock
    private FindDurationPort findDurationPort;

    @InjectMocks
    private DurationGateway gateway;

    private static final UUID ID = UUID.randomUUID();

    private Duration duration;

    @BeforeEach
    void setup() {
        duration = new Duration(ID, 7, LocalDateTime.now());
    }

    @Test
    void findById_shouldReturnDuration_whenExists() {
        // Arrange
        when(findDurationPort.findById(ID)).thenReturn(Optional.of(duration));
        // Act
        Optional<Duration> result = gateway.findById(ID);
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(duration);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        // Arrange
        when(findDurationPort.findById(ID)).thenReturn(Optional.empty());
        // Act
        Optional<Duration> result = gateway.findById(ID);
        // Assert
        assertThat(result).isEmpty();
    }
}