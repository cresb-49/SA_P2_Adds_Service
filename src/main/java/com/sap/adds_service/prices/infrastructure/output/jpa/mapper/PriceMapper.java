package com.sap.adds_service.prices.infrastructure.output.jpa.mapper;

import com.sap.adds_service.prices.domain.Price;
import com.sap.adds_service.prices.infrastructure.output.jpa.entity.PriceEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PriceMapper {

    public Price toDomain(PriceEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Price(
                entity.getId(),
                entity.getCinemaId(),
                entity.getAmountTextBanner(),
                entity.getAmountMediaVertical(),
                entity.getAmountMediaHorizontal(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public PriceEntity toEntity(Price domain) {
        if (domain == null) {
            return null;
        }
        return new PriceEntity(
                domain.getId(),
                domain.getCinemaId(),
                domain.getAmountTextBanner(),
                domain.getAmountMediaVertical(),
                domain.getAmountMediaHorizontal(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
