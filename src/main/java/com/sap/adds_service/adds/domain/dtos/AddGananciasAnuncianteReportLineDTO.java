package com.sap.adds_service.adds.domain.dtos;

import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class AddGananciasAnuncianteReportLineDTO {
    UUID id;
    AddType type;
    LocalDateTime paidAt;
    BigDecimal price;
    LocalDateTime addExpiration;
    String userFullName;

    public AddGananciasAnuncianteReportLineDTO(Add add) {
        this.id = add.getId();
        this.type = add.getType();
        this.paidAt = add.getPaidAt();
        this.price = add.getPrice();
        this.addExpiration = add.getAddExpiration();
        this.userFullName = add.getUserFullName();
    }
}
