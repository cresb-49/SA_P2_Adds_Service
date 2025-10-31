package com.sap.adds_service.adds.infrastructure.output.web.mapper;

import com.sap.adds_service.adds.domain.dtos.CinemaView;
import com.sap.adds_service.common.infrastructure.output.dtos.CinemaResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CinemaViewMapper {

    public CinemaView toDomain(CinemaResponseDTO cinemaResponseDTO) {
        return new CinemaView(
                cinemaResponseDTO.id(),
                cinemaResponseDTO.name()
        );
    }

    public java.util.List<CinemaView> toDomainList(java.util.List<CinemaResponseDTO> cinemaResponseDTOs) {
        return cinemaResponseDTOs.stream()
                .map(this::toDomain)
                .toList();
    }
}
