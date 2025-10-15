package com.sap.adds_service.adds.infrastructure.output.domain.mapper;

import com.sap.adds_service.adds.domain.PriceView;
import com.sap.adds_service.prices.domain.Price;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PriceViewMapper {

    public PriceView toDomain(Price price) {
        if (price == null) return null;
        return new PriceView(
                price.getId(),
                price.getCinemaId(),
                price.getAmountTextBanner(),
                price.getAmountMediaVertical(),
                price.getAmountMediaHorizontal(),
                price.getCreatedAt(),
                price.getUpdatedAt()
        );
    }
}
