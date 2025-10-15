package com.sap.adds_service.prices.application.output;

import com.sap.adds_service.prices.domain.Price;

public interface SavePricePort {
    Price save(Price price);
}
