package com.sap.adds_service.adds.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddTest {

    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String DESC = "Publicidad principal";
    private static final String CONTENT = "Compra tus boletos ahora";
    private static final String CONTENT_TYPE = "image/png";
    private static final String URL = "http://cdn.example.com/banner.png";
    private static final int DAYS = 7;
    private static final BigDecimal PRICE = new BigDecimal("19.99");

    @Test
    @DisplayName("constructor: inicializa y calcula expiración cuando DAYS>0")
    void constructor_shouldInitialize_withExpiration() {
        // Arrange
        // Act
        Add add = new Add(null, AddType.MEDIA_HORIZONTAL, CONTENT_TYPE, true, URL, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Assert
        assertThat(add.getId()).isNotNull();
        assertThat(add.isActive()).isFalse();
        assertThat(add.getPaymentState()).isEqualTo(PaymentState.PENDING);
        assertThat(add.getPrice()).isEqualTo(PRICE);
        assertThat(add.getCreatedAt()).isNotNull();
        assertThat(add.getUpdatedAt()).isNotNull();
        assertThat(add.getAddExpiration()).isEqualTo(add.getCreatedAt().plusDays(DAYS));
    }

    @Test
    @DisplayName("constructor: sin expiración cuando DAYS<=0")
    void constructor_shouldInitialize_withoutExpiration() {
        // Arrange
        int zeroDays = 0;
        // Act
        Add add = new Add(null, AddType.MEDIA_VERTICAL, CONTENT_TYPE, true, URL, DESC, CINEMA_ID, USER_ID, zeroDays, PRICE);
        // Assert
        assertThat(add.getAddExpiration()).isNull();
    }

    @Test
    @DisplayName("validate MEDIA: válido")
    void validate_media_ok() {
        // Arrange
        Add add = new Add(null, AddType.MEDIA_HORIZONTAL, CONTENT_TYPE, true, URL, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act
        add.validate();
        // Assert
        assertThat(add.getType()).isEqualTo(AddType.MEDIA_HORIZONTAL);
    }

    @Test
    @DisplayName("validate TEXT_BANNER: válido")
    void validate_text_ok() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act
        add.validate();
        // Assert
        assertThat(add.getType()).isEqualTo(AddType.TEXT_BANNER);
    }

    @Test
    @DisplayName("validate: descripción nula lanza")
    void validate_descriptionNull_shouldThrow() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, null, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate: descripción vacía lanza")
    void validate_descriptionEmpty_shouldThrow() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, "", CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate: descripción > 255 lanza")
    void validate_descriptionTooLong_shouldThrow() {
        // Arrange
        String longDesc = "x".repeat(256);
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, longDesc, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate: cinemaId nulo lanza")
    void validate_cinemaNull_shouldThrow() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, null, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate: type nulo lanza")
    void validate_typeNull_shouldThrow() {
        // Arrange
        Add add = new Add(CONTENT, null, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate MEDIA: content presente no permitido")
    void validate_media_withContent_shouldThrow() {
        // Arrange
        Add add = new Add("no permitido", AddType.MEDIA_VERTICAL, CONTENT_TYPE, true, URL, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate MEDIA: urlContent faltante")
    void validate_media_missingUrl_shouldThrow() {
        // Arrange
        Add add = new Add(null, AddType.MEDIA_HORIZONTAL, CONTENT_TYPE, true, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate MEDIA: contentType faltante")
    void validate_media_missingContentType_shouldThrow() {
        // Arrange
        Add add = new Add(null, AddType.MEDIA_HORIZONTAL, null, true, URL, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate TEXT_BANNER: content faltante")
    void validate_text_missingContent_shouldThrow() {
        // Arrange
        Add add = new Add(null, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate TEXT_BANNER: content > 255")
    void validate_text_contentTooLong_shouldThrow() {
        // Arrange
        String longContent = "y".repeat(256);
        Add add = new Add(longContent, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate TEXT_BANNER: urlContent no permitido")
    void validate_text_withUrl_shouldThrow() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, URL, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("validate: userId nulo lanza al final")
    void validate_userNull_shouldThrow() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, null, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("changeActive: lanza si no pagado y alterna cuando COMPLETED")
    void changeActive_rules() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::changeActive).isInstanceOf(IllegalStateException.class);
        // Arrange
        add.markAsPaid();
        boolean before = add.isActive();
        LocalDateTime updatedBefore = add.getUpdatedAt();
        // Act
        add.changeActive();
        // Assert
        assertThat(add.isActive()).isEqualTo(!before);
        assertThat(add.getUpdatedAt()).isAfterOrEqualTo(updatedBefore);
    }

    @Test
    @DisplayName("markAsPaid: PENDING->COMPLETED activa y setea paidAt")
    void markAsPaid_fromPending() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act
        add.markAsPaid();
        // Assert
        assertThat(add.getPaymentState()).isEqualTo(PaymentState.COMPLETED);
        assertThat(add.isActive()).isTrue();
        assertThat(add.getPaidAt()).isNotNull();
    }

    @Test
    @DisplayName("markAsPaid: desde FAILED lanza")
    void markAsPaid_fromFailed_shouldThrow() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        add.markAsFailed();
        // Act & Assert
        assertThatThrownBy(add::markAsPaid).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("markAsPaid: si ya COMPLETED lanza")
    void markAsPaid_whenAlreadyCompleted_shouldThrow() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        add.markAsPaid();
        // Act & Assert
        assertThatThrownBy(add::markAsPaid).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("markAsFailed: PENDING->FAILED desactiva")
    void markAsFailed_fromPending() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act
        add.markAsFailed();
        // Assert
        assertThat(add.getPaymentState()).isEqualTo(PaymentState.FAILED);
        assertThat(add.isActive()).isFalse();
    }

    @Test
    @DisplayName("markAsFailed: cuando COMPLETED lanza")
    void markAsFailed_fromCompleted_shouldThrow() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        add.markAsPaid();
        // Act & Assert
        assertThatThrownBy(add::markAsFailed).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("markAsFailed: doble FAILED lanza")
    void markAsFailed_whenAlreadyFailed_shouldThrow() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        add.markAsFailed();
        // Act & Assert
        assertThatThrownBy(add::markAsFailed).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("retryPayment: sólo desde FAILED vuelve a PENDING")
    void retryPayment_rules() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        // Act & Assert
        assertThatThrownBy(add::retryPayment).isInstanceOf(IllegalStateException.class);
        // Arrange
        add.markAsFailed();
        // Act
        add.retryPayment();
        // Assert
        assertThat(add.getPaymentState()).isEqualTo(PaymentState.PENDING);
        assertThat(add.isActive()).isFalse();
    }

    @Test
    @DisplayName("update: modifica solo valores no nulos o cuando cambia externalMedia")
    void update_shouldModifyAndTouchUpdatedAt() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        LocalDateTime before = add.getUpdatedAt();
        // Act
        add.update("Nuevo", "Nueva desc", CONTENT_TYPE, true, URL);
        // Assert
        assertThat(add.getContent()).isEqualTo("Nuevo");
        assertThat(add.getDescription()).isEqualTo("Nueva desc");
        assertThat(add.getContentType()).isEqualTo(CONTENT_TYPE);
        assertThat(add.isExternalMedia()).isTrue();
        assertThat(add.getUrlContent()).isEqualTo(URL);
        assertThat(add.getUpdatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    @DisplayName("update: sin cambios no toca updatedAt")
    void update_noChanges_shouldNotTouchUpdatedAt() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        LocalDateTime before = add.getUpdatedAt();
        // Act
        add.update(null, null, null, false, null);
        // Assert
        assertThat(add.getUpdatedAt()).isEqualTo(before);
    }

    @Test
    @DisplayName("update: sólo cambia externalMedia actualiza updatedAt")
    void update_onlyExternalMedia_shouldTouchUpdatedAt() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, true, null, DESC, CINEMA_ID, USER_ID, DAYS, PRICE);
        LocalDateTime before = add.getUpdatedAt();
        // Act
        add.update(null, null, null, false, null);
        // Assert
        assertThat(add.isExternalMedia()).isFalse();
        assertThat(add.getUpdatedAt()).isAfterOrEqualTo(before);
    }
}