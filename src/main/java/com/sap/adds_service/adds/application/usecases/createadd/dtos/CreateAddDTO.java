package com.sap.adds_service.adds.application.usecases.createadd.dtos;

import com.sap.adds_service.adds.domain.AddType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateAddDTO {
    private String content;
    private AddType type;
    private String description;
    private UUID cinemaId;
    private MultipartFile file;
}
