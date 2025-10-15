package com.sap.adds_service.duration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Duration {
    private UUID id;
    private int days;
    private LocalDateTime createdAt;

    public Duration(int days) {
        this.id = UUID.randomUUID();
        this.days = days;
        this.createdAt = LocalDateTime.now();
    }

    public void validate() {
        if (days <= 0) {
            throw new IllegalArgumentException("Duration in days must be positive");
        }
    }
}
