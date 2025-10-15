package com.sap.adds_service.prices.infrastructure.input.web.mapper;

import com.sap.adds_service.prices.domain.Price;
import com.sap.common_lib.dto.response.add.PriceResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PriceResponseMapper {

    public PriceResponseDTO toResponseDTO(Price price){
        if(price == null) return null;
        return new PriceResponseDTO(
                price.getId(),
                price.getCinemaId(),
                price.getAmountTextBanner(),
                price.getAmountMediaVertical(),
                price.getAmountMediaHorizontal(),
                price.getCreatedAt(),
                price.getUpdatedAt()
        );
    }

    public List<PriceResponseDTO> toResponseDTOList(List<Price> prices){
        return prices.stream().map(this::toResponseDTO).toList();
    }

    public Page<PriceResponseDTO> toResponseDTOPage(Page<Price> pricePage){
        return pricePage.map(this::toResponseDTO);
    }
}
