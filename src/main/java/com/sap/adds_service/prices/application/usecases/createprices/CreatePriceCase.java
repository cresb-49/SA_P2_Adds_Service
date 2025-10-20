package com.sap.adds_service.prices.application.usecases.createprices;

import com.sap.adds_service.prices.application.input.CreatePriceCasePort;
import com.sap.adds_service.prices.application.output.FindCinemaPort;
import com.sap.adds_service.prices.application.output.FindPricePort;
import com.sap.adds_service.prices.application.output.SavePricePort;
import com.sap.adds_service.prices.domain.Price;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CreatePriceCase implements CreatePriceCasePort {

    private final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(200.00);

    private final FindPricePort findPricePort;
    private final SavePricePort savePricePort;
    private final FindCinemaPort findCinemaPort;

    @Override
    public Price createPrices(UUID cinemaId) {
        // Check if cinema exists
        if (!findCinemaPort.checkIfCinemaExistsById(cinemaId)) {
            throw new IllegalArgumentException("El cine no existe");
        }
        // Check if prices already exist for the cinema
        if (findPricePort.checkIfCinemaExistsById(cinemaId)) {
            throw new IllegalArgumentException("Ya existen precios para este cine");
        }
        var price = new Price(
                cinemaId,
                DEFAULT_PRICE,
                DEFAULT_PRICE,
                DEFAULT_PRICE
        );
        // Validate the price
        price.validate();
        // Save the price
        return savePricePort.save(price);
    }
}
