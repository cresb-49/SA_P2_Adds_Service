package com.sap.adds_service.adds.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Add {

    private final int CONTENT_MAX_LENGTH = 255;
    private final int DESCRIPTION_MAX_LENGTH = 255;

    private UUID id;
    private String content;
    private AddType type;
    private String urlContent;
    private boolean active;
    private String description;
    private UUID cinemaId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Add(String content, AddType type, String urlContent, String description, UUID cinemaId) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.type = type;
        this.urlContent = urlContent;
        this.description = description;
        this.cinemaId = cinemaId;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void assignUrlContent(String urlContent) {
        this.urlContent = urlContent;
    }

    public void validate() {
        if( this.description == null || this.description.isEmpty()) {
            throw new RuntimeException("Description is required");
        }
        if(this.description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new RuntimeException("Description must be less than 255 characters");
        }
        if( this.cinemaId == null ) {
            throw new RuntimeException("Cinema ID is required");
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

    private void validationsMediaType() {
        if (this.content != null && !this.content.isEmpty()) {
            throw new RuntimeException("Content must be empty for poster types");
        }
        if (this.urlContent == null || this.urlContent.isEmpty()) {
            throw new RuntimeException("Media is required for poster types");
        }
    }

    private void validationsTextBanner() {
        if( this.content == null || this.content.isEmpty()) {
            throw new RuntimeException("Content is required for text banner");
        }
        if (this.content.length() > CONTENT_MAX_LENGTH) {
            throw new RuntimeException("Content must be less than 255 characters");
        }
        if( this.urlContent != null && !this.urlContent.isEmpty()) {
            throw new RuntimeException("Media is not allowed for text banner");
        }
    }
}
