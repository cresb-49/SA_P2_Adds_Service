package com.sap.adds_service.adds.application.output;

import com.sap.adds_service.adds.domain.Add;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FindPurchasedAdds {
    List<Add> findPurchasedAdds(
            LocalDateTime from,
            LocalDateTime to,
            String type,
            LocalDate periodFrom,
            LocalDate periodTo,
            String paymentState
    );
}
