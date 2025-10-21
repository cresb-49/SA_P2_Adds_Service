package com.sap.adds_service.adds.domain;

import com.sap.adds_service.adds.domain.dtos.CinemaView;
import com.sap.adds_service.adds.domain.dtos.UserView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Add {

    private final int CONTENT_MAX_LENGTH = 255;
    private final int DESCRIPTION_MAX_LENGTH = 255;

    private final UUID id;
    private String content;
    private final AddType type;
    private String contentType;
    private boolean externalMedia;
    private String urlContent;
    private boolean active;
    private String description;
    private final UUID cinemaId;
    private final UUID userId;
    private PaymentState paymentState;
    private LocalDateTime paidAt;
    private final BigDecimal price;
    private final LocalDateTime addExpiration;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Optional fields used by Factory pattern
    @Setter
    private CinemaView cinema;
    @Setter
    private UserView user;

    public Add(
            UUID id,
            String content,
            AddType type,
            String contentType,
            boolean externalMedia,
            String urlContent,
            boolean active,
            String description,
            UUID cinemaId,
            UUID userId,
            PaymentState paymentState,
            LocalDateTime paidAt,
            BigDecimal price,
            LocalDateTime addExpiration,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.content = content;
        this.type = type;
        this.contentType = contentType;
        this.externalMedia = externalMedia;
        this.urlContent = urlContent;
        this.active = active;
        this.description = description;
        this.cinemaId = cinemaId;
        this.userId = userId;
        this.paymentState = paymentState;
        this.paidAt = paidAt;
        this.price = price;
        this.addExpiration = addExpiration;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Constructor for creating a new Add entity.
     *
     * @param content
     * @param type
     * @param contentType
     * @param externalMedia
     * @param urlContent
     * @param description
     * @param cinemaId
     * @param userId
     * @param durationDays
     * @param price
     */
    public Add(String content, AddType type, String contentType, boolean externalMedia,
               String urlContent, String description, UUID cinemaId, UUID userId, int durationDays, BigDecimal price
    ) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.type = type;
        this.contentType = contentType;
        this.externalMedia = externalMedia;
        this.urlContent = urlContent;
        this.active = false;
        this.description = description;
        this.cinemaId = cinemaId;
        this.userId = userId;
        this.paymentState = PaymentState.PENDING;
        this.paidAt = null;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.addExpiration = durationDays > 0 ? this.createdAt.plusDays(durationDays) : null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Toggle the active status of the Add entity.
     */
    public void changeActive() {
        if (this.paymentState != PaymentState.COMPLETED) {
            throw new IllegalStateException("No se puede cambiar el estado de un anuncio no pagado. Add ID: " + this.id);
        }
        this.active = !this.active;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark the Add entity as paid, setting its payment state to COMPLETED and activating it.
     */
    public void markAsPaid() {
        if (this.paymentState == PaymentState.COMPLETED) {
            throw new IllegalStateException("Add ID:" + this.id + " ya está marcado como pagado");
        }
        if (this.paymentState == PaymentState.FAILED) {
            throw new IllegalStateException("No se puede marcar un pago fallido como pagado directamente. Por favor, intente el pago primero. Add ID: " + this.id);
        }
        this.paymentState = PaymentState.COMPLETED;
        this.active = true;
        this.paidAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark the Add entity as failed, setting its payment state to FAILED and deactivating it.
     */
    public void markAsFailed() {
        if (this.paymentState == PaymentState.COMPLETED) {
            throw new IllegalStateException("No se puede marcar un pago completado como fallido. Add ID: " + this.id);
        }
        if (this.paymentState == PaymentState.FAILED) {
            throw new IllegalStateException("El anuncio ya está marcado como fallido. Add ID: " + this.id);
        }
        this.paymentState = PaymentState.FAILED;
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Retry the payment for the Add entity, setting its payment state back to PENDING and deactivating it.
     */
    public void retryPayment() {
        if (this.paymentState != PaymentState.FAILED) {
            throw new IllegalStateException("Solo se puede reintentar el pago de un anuncio fallido. Add ID: " + this.id);
        }
        this.paymentState = PaymentState.PENDING;
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update the Add entity with new values. Only non-null values will be updated.
     *
     * @param content
     * @param description
     * @param urlContent
     */
    public void update(String content, String description, String contentType, boolean externalMedia, String urlContent) {
        var updateFlag = false;
        if (content != null) {
            this.content = content;
            updateFlag = true;
        }
        if (description != null) {
            this.description = description;
            updateFlag = true;
        }
        if (contentType != null) {
            this.contentType = contentType;
            updateFlag = true;
        }
        if (externalMedia != this.externalMedia) {
            this.externalMedia = externalMedia;
            updateFlag = true;
        }
        if (urlContent != null) {
            this.urlContent = urlContent;
            updateFlag = true;
        }
        if (updateFlag) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Validate the Add entity based on its type and attributes.
     */
    public void validate() {
        if (this.description == null || this.description.isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        if (this.description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("La descripción debe tener menos de 255 caracteres");
        }
        if (this.cinemaId == null) {
            throw new IllegalArgumentException("El Cinema ID es obligatorio");
        }
        if (this.type == null) {
            throw new IllegalArgumentException("El tipo de anuncio es obligatorio");
        }
        switch (this.type) {
            case MEDIA_HORIZONTAL, MEDIA_VERTICAL -> {
                this.validationsMediaType();
            }
            case TEXT_BANNER -> {
                this.validationsTextBanner();
            }
            default -> throw new IllegalStateException("Tipo de anuncio desconocido: " + this.type);
        }
        if (this.userId == null) {
            throw new IllegalArgumentException("El User ID es obligatorio");
        }
    }

    /**
     * Validations for media types (horizontal and vertical).
     */
    private void validationsMediaType() {
        if (this.content != null && !this.content.isEmpty()) {
            throw new IllegalArgumentException("El contenido debe estar vacío para los tipos de medios");
        }
        if (this.urlContent == null || this.urlContent.isEmpty()) {
            throw new IllegalArgumentException("El medio es obligatorio para los tipos de medios");
        }
        if (this.contentType == null || this.contentType.isEmpty()) {
            throw new IllegalArgumentException("El tipo de contenido es obligatorio para los tipos de medios");
        }
    }

    /**
     * Validations for text banner type.
     */
    private void validationsTextBanner() {
        if (this.content == null || this.content.isEmpty()) {
            throw new IllegalArgumentException("El contenido es obligatorio para el banner de texto");
        }
        if (this.content.length() > CONTENT_MAX_LENGTH) {
            throw new IllegalArgumentException("El contenido debe tener menos de 255 caracteres");
        }
        if (this.urlContent != null && !this.urlContent.isEmpty()) {
            throw new IllegalArgumentException("El medio no está permitido para el banner de texto");
        }
    }
}
