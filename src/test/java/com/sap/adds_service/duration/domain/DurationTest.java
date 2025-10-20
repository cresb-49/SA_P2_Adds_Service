

package com.sap.adds_service.duration.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DurationTest {

    private static final int VALID_DAYS = 5;
    private static final int INVALID_DAYS_ZERO = 0;
    private static final int INVALID_DAYS_NEGATIVE = -3;

    @Test
    @DisplayName("constructor: genera id y fecha actual")
    void constructor_shouldGenerateIdAndCreatedAt() {
        // Arrange
        // Act
        Duration duration = new Duration(VALID_DAYS);
        // Assert
        assertThat(duration.getId()).isNotNull();
        assertThat(duration.getDays()).isEqualTo(VALID_DAYS);
        assertThat(duration.getCreatedAt()).isNotNull();
        assertThat(duration.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("validate: días válidos no lanza excepción")
    void validate_validDays_shouldNotThrow() {
        // Arrange
        Duration duration = new Duration(VALID_DAYS);
        // Act & Assert
        duration.validate();
        assertThat(duration.getDays()).isEqualTo(VALID_DAYS);
    }

    @Test
    @DisplayName("validate: días = 0 lanza excepción")
    void validate_zeroDays_shouldThrow() {
        // Arrange
        Duration duration = new Duration(INVALID_DAYS_ZERO);
        // Act & Assert
        assertThatThrownBy(duration::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate: días negativos lanza excepción")
    void validate_negativeDays_shouldThrow() {
        // Arrange
        Duration duration = new Duration(INVALID_DAYS_NEGATIVE);
        // Act & Assert
        assertThatThrownBy(duration::validate).isInstanceOf(IllegalArgumentException.class);
    }
}