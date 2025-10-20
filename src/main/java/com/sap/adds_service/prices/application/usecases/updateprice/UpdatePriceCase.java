package com.sap.adds_service.prices.application.usecases.updateprice;

import com.sap.adds_service.prices.application.input.UpdatePriceCasePort;
import com.sap.adds_service.prices.application.output.FindPricePort;
import com.sap.adds_service.prices.application.output.SavePricePort;
import com.sap.adds_service.prices.application.usecases.updateprice.dtos.UpdatePriceDTO;
import com.sap.adds_service.prices.domain.Price;
import com.sap.common_lib.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UpdatePriceCase implements UpdatePriceCasePort {

    private final FindPricePort findPricePort;
    private final SavePricePort savePricePort;

    @Override
    public Price update(UpdatePriceDTO updatePriceDTO) {
        var price = findPricePort.findById(updatePriceDTO.id()).orElseThrow(
                () -> new NotFoundException("Precio no encontrado")
        );
        // update the price
        price.update(
                updatePriceDTO.amountTextBanner(),
                updatePriceDTO.amountMediaVertical(),
                updatePriceDTO.amountMediaHorizontal()
        );
        // validate the price
        price.validate();
        // save the price
        return savePricePort.save(price);
    }
}
