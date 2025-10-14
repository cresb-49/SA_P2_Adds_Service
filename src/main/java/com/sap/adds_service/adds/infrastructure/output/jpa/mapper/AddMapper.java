package com.sap.adds_service.adds.infrastructure.output.jpa.mapper;

import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.infrastructure.output.jpa.entity.AddEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AddMapper {

    public Add toDomain(AddEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Add(
                entity.getId(),
                entity.getContent(),
                entity.getType(),
                entity.getContentType(),
                entity.isExternalMedia(),
                entity.getUrlContent(),
                entity.isActive(),
                entity.getDescription(),
                entity.getCinemaId(),
                entity.getUserId(),
                entity.getAddExpiration(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public AddEntity toEntity(Add add) {
        if (add == null) {
            return null;
        }
        return new AddEntity(
                add.getId(),
                add.getContent(),
                add.getType(),
                add.getContentType(),
                add.isExternalMedia(),
                add.getUrlContent(),
                add.isActive(),
                add.getDescription(),
                add.getCinemaId(),
                add.getUserId(),
                add.getAddExpiration(),
                add.getCreatedAt(),
                add.getUpdatedAt()
        );
    }
}
