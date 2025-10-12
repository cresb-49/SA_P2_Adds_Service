package com.sap.adds_service.adds.domain;

import lombok.Builder;
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
    private String urlContent;
    private boolean active;
    private String description;
    private final UUID cinemaId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * All args constructor Add entity
     * @param id
     * @param content
     * @param type
     * @param urlContent
     * @param active
     * @param description
     * @param cinemaId
     * @param createdAt
     * @param updatedAt
     */
    @Builder(toBuilder = true)
    public Add(UUID id, String content, AddType type, String urlContent, Boolean active,
                String description, UUID cinemaId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id != null ? id : UUID.randomUUID();
        this.content = content;
        this.type = type;
        this.urlContent = urlContent;
        this.active = active != null ? active : true;
        this.description = description;
        this.cinemaId = cinemaId;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    /**
     * Create a new Add with default values for id, active, createdAt, and updatedAt.
     * @param content
     * @param type
     * @param urlContent
     * @param description
     * @param cinemaId
     */
    public Add(String content, AddType type, String urlContent, String description, UUID cinemaId) {
        this(null, content, type, urlContent, true, description, cinemaId, null, null);
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
     * @param content
     * @param active
     * @param description
     * @param urlContent
     */
    public void update(String content, Boolean active, String description, String urlContent) {
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
        switch (this.type) {
            case MEDIA_HORIZONTAL, MEDIA_VERTICAL -> {
                this.validationsMediaType();
            }
            case TEXT_BANNER -> {
                this.validationsTextBanner();
            }
            default -> throw new IllegalStateException("Unknown add type: " + this.type);
        }
    }

    /**
     * Validations for media types (horizontal and vertical).
     */
    private void validationsMediaType() {
        if (this.content != null && !this.content.isEmpty()) {
            throw new IllegalArgumentException("Content must be empty for poster types");
        }
        if (this.urlContent == null || this.urlContent.isEmpty()) {
            throw new IllegalArgumentException("Media is required for poster types");
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
