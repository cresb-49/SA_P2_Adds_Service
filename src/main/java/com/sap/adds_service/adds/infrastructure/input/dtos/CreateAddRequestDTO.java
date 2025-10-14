package com.sap.adds_service.adds.infrastructure.input.dtos;

import com.sap.adds_service.adds.application.usecases.createadd.dtos.CreateAddDTO;
import com.sap.adds_service.adds.domain.AddType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CreateAddRequestDTO(
        String content,
        AddType type,
        String description,
        UUID cinemaId,
        String urlContent,
        UUID userId,
        UUID durationDaysId
) {
    public CreateAddDTO toDomain(MultipartFile file) {
        return new CreateAddDTO(content, type, description, cinemaId, urlContent, userId, durationDaysId, file);
    }
}
