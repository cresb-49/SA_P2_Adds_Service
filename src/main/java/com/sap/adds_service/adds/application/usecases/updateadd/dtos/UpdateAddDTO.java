package com.sap.adds_service.adds.application.usecases.updateadd.dtos;

import com.sap.adds_service.adds.domain.AddType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UpdateAddDTO(
        UUID id,
        String content,
        Boolean active,
        String description,
        String urlContent,
        MultipartFile file
) {}