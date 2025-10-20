package com.sap.adds_service.prices.application.usecases.findprice;

import com.sap.adds_service.prices.application.input.FindPriceCasePort;
import com.sap.adds_service.prices.application.output.FindPricePort;
import com.sap.adds_service.prices.domain.Price;
import com.sap.common_lib.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class FindPriceCase implements FindPriceCasePort {

    private final FindPricePort findPricePort;

    @Override
    public Price findById(UUID id) {
        return findPricePort.findById(id).orElseThrow(
                () -> new NotFoundException("Precio no encontrado")
        );
    }

    @Override
    public Price findByCinemaId(UUID cinemaId) {
        return findPricePort.findByCinemaId(cinemaId).orElseThrow(
                () -> new NotFoundException("Precio no encontrado para el cine con id: " + cinemaId)
        );
    }

    @Override
    public Page<Price> findAll(int page) {
        return findPricePort.findAll(page);
    }
}
