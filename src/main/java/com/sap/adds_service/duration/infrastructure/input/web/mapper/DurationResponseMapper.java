package com.sap.adds_service.duration.infrastructure.input.web.mapper;

import com.sap.adds_service.duration.domain.Duration;
import com.sap.common_lib.dto.response.add.DurationResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DurationResponseMapper {
    public DurationResponseDTO toResponseDTO(Duration duration) {
        if (duration == null) return null;
        return new DurationResponseDTO(
                duration.getId(),
                duration.getDays(),
                duration.getCreatedAt()
        );
    }

    public List<DurationResponseDTO> toResponseDTOList(List<Duration> durations) {
        return durations.stream().map(this::toResponseDTO).toList();
    }
}
