package com.sap.adds_service.adds.infrastructure.input.web.dtos;

import com.sap.adds_service.adds.application.usecases.updateadd.dtos.UpdateAddDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UpdateAddRequestDTO(
        String content,
        String description,
        String urlContent
) {

    public UpdateAddDTO toDomain(UUID id, MultipartFile file) {
        return new UpdateAddDTO(id, content, description, urlContent, file);
    }
}
