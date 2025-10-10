package com.sap.adds_service.adds.infrastructure.input.mappers;

import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.infrastructure.input.dtos.AddResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class AddResponseMapper {

    public AddResponseDTO toResponse(Add add) {
        if (add == null) return null;
        return new AddResponseDTO(
                add.getId(),
                add.getContent(),
                add.getType().toString(),
                add.getUrlContent(),
                add.isActive(),
                add.getDescription(),
                add.getCinemaId(),
                add.getCreatedAt(),
                add.getUpdatedAt()
        );
    }

    public List<AddResponseDTO> toResponseList(List<Add> adds) {
        return adds.stream().map(this::toResponse).toList();
    }

    public Page<AddResponseDTO> toResponsePage(Page<Add> adds) {
        return adds.map(this::toResponse);
    }
}
