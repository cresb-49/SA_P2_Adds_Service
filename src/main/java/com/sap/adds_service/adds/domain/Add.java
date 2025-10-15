package com.sap.adds_service.adds.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
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
            throw new IllegalStateException("Cannot change active status of an unpaid add");
        }
        this.active = !this.active;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark the Add entity as paid, setting its payment state to COMPLETED and activating it.
     */
    public void markAsPaid() {
        if (this.paymentState == PaymentState.COMPLETED) {
            throw new IllegalStateException("Add is already marked as paid");
        }
        if(this.paymentState == PaymentState.FAILED){
            throw new IllegalStateException("Cannot mark a failed payment as paid");
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
            throw new IllegalStateException("Cannot mark a completed payment as failed");
        }
        if(this.paymentState == PaymentState.FAILED){
            throw new IllegalStateException("Add is already marked as failed");
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
            throw new IllegalStateException("Can only retry payment for a failed add");
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
            throw new IllegalArgumentException("Description is required");
        }
        if (this.description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("Description must be less than 255 characters");
        }
        if (this.cinemaId == null) {
            throw new IllegalArgumentException("Cinema ID is required");
        }
        if (this.type == null) {
            throw new IllegalArgumentException("Add type is required");
        }
        switch (this.type) {
            case MEDIA_HORIZONTAL, MEDIA_VERTICAL -> {
                this.validationsMediaType();
            }
            case TEXT_BANNER -> {
                this.validationsTextBanner();
            }
            default -> throw new IllegalStateException("Unknown add type: " + this.type);
        }
        if (this.userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
    }

    /**
     * Validations for media types (horizontal and vertical).
     */
    private void validationsMediaType() {
        if (this.content != null && !this.content.isEmpty()) {
            throw new IllegalArgumentException("Content must be empty for media types");
        }
        if (this.urlContent == null || this.urlContent.isEmpty()) {
            throw new IllegalArgumentException("Media is required for media types");
        }
        if (this.contentType == null || this.contentType.isEmpty()) {
            throw new IllegalArgumentException("Content type is required for media types");
        }
    }

    /**
     * Validations for text banner type.
     */
    private void validationsTextBanner() {
        if (this.content == null || this.content.isEmpty()) {
            throw new IllegalArgumentException("Content is required for text banner");
        }
        if (this.content.length() > CONTENT_MAX_LENGTH) {
            throw new IllegalArgumentException("Content must be less than 255 characters");
        }
        if (this.urlContent != null && !this.urlContent.isEmpty()) {
            throw new IllegalArgumentException("Media is not allowed for text banner");
        }
    }
}
