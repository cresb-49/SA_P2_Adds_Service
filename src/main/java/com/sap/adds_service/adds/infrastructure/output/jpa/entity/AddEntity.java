package com.sap.adds_service.adds.infrastructure.output.jpa.entity;

import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "adds")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private AddType type;

    @Column(nullable = true)
    private String contentType;

    @Column(nullable = false)
    private boolean externalMedia;

    @Column(nullable = false)
    private String urlContent;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private UUID cinemaId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private PaymentState paymentState;

    @Column(nullable = true)
    private LocalDateTime paidAt;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime addExpiration;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
