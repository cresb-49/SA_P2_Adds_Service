package com.sap.adds_service.prices.infrastructure.input.web.dtos;

import com.sap.adds_service.prices.application.usecases.updateprice.dtos.UpdatePriceDTO;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdatePriceRequestDTO(
        BigDecimal amountTextBanner,
        BigDecimal amountMediaVertical,
        BigDecimal amountMediaHorizontal
) {
    public UpdatePriceDTO toDTO(UUID id) {
        return new UpdatePriceDTO(
                id,
                this.amountTextBanner,
                this.amountMediaVertical,
                this.amountMediaHorizontal
        );
    }
}
