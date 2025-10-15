package com.sap.adds_service.prices.application.input;

import com.sap.adds_service.prices.application.usecases.updateprice.dtos.UpdatePriceDTO;
import com.sap.adds_service.prices.domain.Price;

public interface UpdatePriceCasePort {
    Price update(UpdatePriceDTO updatePriceDTO);
}
