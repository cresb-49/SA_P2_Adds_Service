package com.sap.adds_service.adds.domain;

import lombok.Getter;

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
    private final LocalDateTime addExpiration;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Add(String content, AddType type, String contentType, boolean externalMedia,
               String urlContent, String description, UUID cinemaId, UUID userId, int durationDays
    ) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.type = type;
        this.contentType = contentType;
        this.externalMedia = externalMedia;
        this.urlContent = urlContent;
        this.active = true;
        this.description = description;
        this.cinemaId = cinemaId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.addExpiration = durationDays > 0 ? this.createdAt.plusDays(durationDays) : null;
        this.updatedAt = LocalDateTime.now();
    }

    public Add(UUID id, String content, AddType type, String contentType, boolean externalMedia, String urlContent, boolean active,
               String description, UUID cinemaId, UUID userId, LocalDateTime addExpiration, LocalDateTime createdAt, LocalDateTime updatedAt) {
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
        this.createdAt = createdAt;
        this.addExpiration = addExpiration;
        this.updatedAt = updatedAt;
    }

    /**
     * Toggle the active status of the Add entity.
     */
    public void changeActive() {
        this.active = !this.active;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update the Add entity with new values. Only non-null values will be updated.
     *
     * @param content
     * @param active
     * @param description
     * @param urlContent
     */
    public void update(String content, Boolean active, String description, String contentType, boolean externalMedia, String urlContent) {
        var updateFlag = false;
        if (content != null) {
            this.content = content;
            updateFlag = true;
        }
        if (active != null) {
            this.active = active;
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
