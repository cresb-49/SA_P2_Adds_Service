package com.sap.adds_service.prices.application.usecases.deleteprice;

import com.sap.adds_service.prices.application.input.DeletePriceCasePort;
import com.sap.adds_service.prices.application.output.DeletePricePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DeletePriceCase implements DeletePriceCasePort {

    private final DeletePricePort deletePricePort;

    @Override
    public void deleteByCinemaId(UUID cinemaId) {
        deletePricePort.deleteByCinemaId(cinemaId);
    }

    @Override
    public void deleteById(UUID id) {
        deletePricePort.deleteById(id);
    }
}
