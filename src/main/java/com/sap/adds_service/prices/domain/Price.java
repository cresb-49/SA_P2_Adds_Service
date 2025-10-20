package com.sap.adds_service.prices.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Price {
    private final UUID id;
    private final UUID cinemaId;
    private BigDecimal amountTextBanner;
    private BigDecimal amountMediaVertical;
    private BigDecimal amountMediaHorizontal;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Price(UUID cinemaId, BigDecimal amountTextBanner, BigDecimal amountMediaVertical, BigDecimal amountMediaHorizontal) {
        this.id = UUID.randomUUID();
        this.cinemaId = cinemaId;
        this.amountTextBanner = amountTextBanner;
        this.amountMediaVertical = amountMediaVertical;
        this.amountMediaHorizontal = amountMediaHorizontal;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(BigDecimal amountTextBanner, BigDecimal amountMediaVertical, BigDecimal amountMediaHorizontal) {
        if (amountTextBanner != null) {
            this.amountTextBanner = amountTextBanner;
        }
        if (amountMediaVertical != null) {
            this.amountMediaVertical = amountMediaVertical;
        }
        if (amountMediaHorizontal != null) {
            this.amountMediaHorizontal = amountMediaHorizontal;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void validate() {
        if (cinemaId == null) {
            throw new IllegalArgumentException("El ID del cine no puede ser nulo");
        }
        if (amountTextBanner == null || amountTextBanner.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto del banner de texto debe ser mayor a cero");
        }
        if (amountMediaVertical == null || amountMediaVertical.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto del banner de medios vertical debe ser mayor a cero");
        }
        if (amountMediaHorizontal == null || amountMediaHorizontal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto del banner de medios horizontal debe ser mayor a cero");
        }
    }
}
