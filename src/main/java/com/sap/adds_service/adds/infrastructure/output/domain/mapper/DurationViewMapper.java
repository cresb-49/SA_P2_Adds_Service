package com.sap.adds_service.adds.infrastructure.output.domain.mapper;

import com.sap.adds_service.adds.domain.DurationView;
import com.sap.adds_service.duration.domain.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DurationViewMapper {
    public DurationView toDomain(Duration duration) {
        if (duration == null) return null;
        return new DurationView(
                duration.getId(),
                duration.getDays(),
                duration.getCreatedAt()
        );
    }
}
