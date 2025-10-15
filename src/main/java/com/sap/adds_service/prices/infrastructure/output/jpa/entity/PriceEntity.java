package com.sap.adds_service.prices.infrastructure.output.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PriceEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID cinemaId;

    @Column(nullable = false)
    private BigDecimal amountTextBanner;

    @Column(nullable = false)
    private BigDecimal amountMediaVertical;

    @Column(nullable = false)
    private BigDecimal amountMediaHorizontal;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
