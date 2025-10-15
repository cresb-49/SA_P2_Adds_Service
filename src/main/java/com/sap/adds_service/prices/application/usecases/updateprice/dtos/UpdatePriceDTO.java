package com.sap.adds_service.prices.application.usecases.updateprice.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdatePriceDTO(
        UUID id,
        BigDecimal amountTextBanner,
        BigDecimal amountMediaVertical,
        BigDecimal amountMediaHorizontal
) {
}
