package com.sap.adds_service.adds.application.usecases.updateadd.dtos;

import com.sap.adds_service.adds.domain.AddType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UpdateAddDTO {
    private UUID id;
    private String content;
    private boolean active;
    private String description;
    private MultipartFile file;
}
