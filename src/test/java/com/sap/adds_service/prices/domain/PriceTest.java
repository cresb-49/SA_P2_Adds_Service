

package com.sap.adds_service.prices.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final BigDecimal TXT = new BigDecimal("10.00");
    private static final BigDecimal VERT = new BigDecimal("20.00");
    private static final BigDecimal HORZ = new BigDecimal("30.00");

    @Test
    @DisplayName("constructor: inicializa id, fechas y montos")
    void constructor_shouldInitializeFields() {
        // Arrange
        // Act
        Price price = new Price(CINEMA_ID, TXT, VERT, HORZ);
        // Assert
        assertThat(price.getId()).isNotNull();
        assertThat(price.getCinemaId()).isEqualTo(CINEMA_ID);
        assertThat(price.getAmountTextBanner()).isEqualTo(TXT);
        assertThat(price.getAmountMediaVertical()).isEqualTo(VERT);
        assertThat(price.getAmountMediaHorizontal()).isEqualTo(HORZ);
        assertThat(price.getCreatedAt()).isNotNull();
        assertThat(price.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("validate: datos válidos no lanzan excepción")
    void validate_validData_shouldNotThrow() {
        // Arrange
        Price price = new Price(CINEMA_ID, TXT, VERT, HORZ);
        // Act
        price.validate();
        // Assert
        assertThat(price.getCinemaId()).isEqualTo(CINEMA_ID);
    }

    @Test
    @DisplayName("validate: cinemaId nulo lanza excepción")
    void validate_nullCinema_shouldThrow() {
        // Arrange
        Price price = new Price(null, TXT, VERT, HORZ);
        // Act & Assert
        assertThatThrownBy(price::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate: amountTextBanner negativo lanza; cero es válido según implementación actual")
    void validate_textBannerAmount_rules() {
        // Arrange
        Price negative = new Price(CINEMA_ID, new BigDecimal("-0.01"), VERT, HORZ);
        Price zero = new Price(CINEMA_ID, BigDecimal.ZERO, VERT, HORZ);
        // Act & Assert
        assertThatThrownBy(negative::validate).isInstanceOf(IllegalArgumentException.class);
        zero.validate();
        assertThat(zero.getAmountTextBanner()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("validate: amountMediaVertical negativo lanza; cero válido")
    void validate_verticalAmount_rules() {
        // Arrange
        Price negative = new Price(CINEMA_ID, TXT, new BigDecimal("-1.00"), HORZ);
        Price zero = new Price(CINEMA_ID, TXT, BigDecimal.ZERO, HORZ);
        // Act & Assert
        assertThatThrownBy(negative::validate).isInstanceOf(IllegalArgumentException.class);
        zero.validate();
        assertThat(zero.getAmountMediaVertical()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("validate: amountMediaHorizontal negativo lanza; cero válido")
    void validate_horizontalAmount_rules() {
        // Arrange
        Price negative = new Price(CINEMA_ID, TXT, VERT, new BigDecimal("-2.00"));
        Price zero = new Price(CINEMA_ID, TXT, VERT, BigDecimal.ZERO);
        // Act & Assert
        assertThatThrownBy(negative::validate).isInstanceOf(IllegalArgumentException.class);
        zero.validate();
        assertThat(zero.getAmountMediaHorizontal()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("update: actualiza solo montos no nulos y siempre toca updatedAt")
    void update_shouldUpdateNonNull_andTouchUpdatedAt() {
        // Arrange
        Price price = new Price(CINEMA_ID, TXT, VERT, HORZ);
        LocalDateTime before = price.getUpdatedAt();
        BigDecimal newTxt = new BigDecimal("11.00");
        BigDecimal newVert = null; // se mantiene
        BigDecimal newHorz = new BigDecimal("33.00");
        // Act
        price.update(newTxt, newVert, newHorz);
        // Assert
        assertThat(price.getAmountTextBanner()).isEqualTo(newTxt);
        assertThat(price.getAmountMediaVertical()).isEqualTo(VERT);
        assertThat(price.getAmountMediaHorizontal()).isEqualTo(newHorz);
        assertThat(price.getUpdatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    @DisplayName("update: con todos nulos no cambia montos pero actualiza updatedAt")
    void update_allNulls_shouldOnlyTouchUpdatedAt() {
        // Arrange
        Price price = new Price(CINEMA_ID, TXT, VERT, HORZ);
        LocalDateTime before = price.getUpdatedAt();
        // Act
        price.update(null, null, null);
        // Assert
        assertThat(price.getAmountTextBanner()).isEqualTo(TXT);
        assertThat(price.getAmountMediaVertical()).isEqualTo(VERT);
        assertThat(price.getAmountMediaHorizontal()).isEqualTo(HORZ);
        assertThat(price.getUpdatedAt()).isAfterOrEqualTo(before);
    }
}