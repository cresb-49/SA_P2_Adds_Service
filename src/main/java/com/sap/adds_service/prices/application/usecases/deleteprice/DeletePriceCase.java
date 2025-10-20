package com.sap.adds_service.prices.application.usecases.deleteprice;

import com.sap.adds_service.prices.application.input.DeletePriceCasePort;
import com.sap.adds_service.prices.application.output.DeletePricePort;
import com.sap.adds_service.prices.application.output.FindPricePort;
import com.sap.common_lib.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DeletePriceCase implements DeletePriceCasePort {

    private final DeletePricePort deletePricePort;
    private final FindPricePort findPricePort;

    @Override
    public void deleteByCinemaId(UUID cinemaId) {
        if (!findPricePort.checkIfCinemaExistsById(cinemaId)) {
            throw new NotFoundException("Precio no encontrado para el cine con id: " + cinemaId);
        }
        deletePricePort.deleteByCinemaId(cinemaId);
    }

    @Override
    public void deleteById(UUID id) {
        findPricePort.findById(id).orElseThrow(
                () -> new NotFoundException("Precio no encontrado")
        );
        deletePricePort.deleteById(id);
    }
}
