package com.sap.adds_service.duration.infrastructure.output.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "durations")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DurationEntity {

    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private int days;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
