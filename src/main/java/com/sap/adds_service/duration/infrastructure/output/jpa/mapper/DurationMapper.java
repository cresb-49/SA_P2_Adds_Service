package com.sap.adds_service.duration.infrastructure.output.jpa.mapper;

import com.sap.adds_service.duration.domain.Duration;
import com.sap.adds_service.duration.infrastructure.output.jpa.entity.DurationEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DurationMapper {

    public Duration toDomain(DurationEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Duration(
                entity.getId(),
                entity.getDays(),
                entity.getCreatedAt()
        );
    }

    public DurationEntity toEntity(Duration domain) {
        if (domain == null) {
            return null;
        }
        return new DurationEntity(
                domain.getId(),
                domain.getDays(),
                domain.getCreatedAt()
        );
    }
}
