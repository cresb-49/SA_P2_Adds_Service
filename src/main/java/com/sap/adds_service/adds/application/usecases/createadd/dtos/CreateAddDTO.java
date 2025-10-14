package com.sap.adds_service.adds.application.usecases.createadd.dtos;

import com.sap.adds_service.adds.domain.AddType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CreateAddDTO(
        String content,
        AddType type,
        String description,
        UUID cinemaId,
        String urlContent,
        UUID userId,
        UUID durationDaysId,
        MultipartFile file
) {
}