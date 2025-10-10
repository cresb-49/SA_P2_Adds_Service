package com.sap.adds_service.adds.infrastructure.input.dtos;

import com.sap.adds_service.adds.domain.AddType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AddResponseDTO {
    private UUID id;
    private String content;
    private String type;
    private String urlContent;
    private boolean active;
    private String description;
    private UUID cinemaId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
