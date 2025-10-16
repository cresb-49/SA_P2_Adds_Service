package com.sap.adds_service.prices.infrastructure.input.web.controller;

import com.sap.adds_service.prices.application.input.CreatePriceCasePort;
import com.sap.adds_service.prices.application.input.DeletePriceCasePort;
import com.sap.adds_service.prices.application.input.FindPriceCasePort;
import com.sap.adds_service.prices.application.input.UpdatePriceCasePort;
import com.sap.adds_service.prices.infrastructure.input.web.dtos.UpdatePriceRequestDTO;
import com.sap.adds_service.prices.infrastructure.input.web.mapper.PriceResponseMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prices")
@AllArgsConstructor
public class PriceController {

    private final CreatePriceCasePort createPriceCasePort;
    private final DeletePriceCasePort deletePriceCasePort;
    private final FindPriceCasePort findPriceCasePort;
    private final UpdatePriceCasePort updatePriceCasePort;

    private final PriceResponseMapper priceResponseMapper;

    @PostMapping("/cinema/{cinemaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPrices(
            @PathVariable UUID cinemaId
    ) {
        var price = createPriceCasePort.createPrices(cinemaId);
        return ResponseEntity.ok(priceResponseMapper.toResponseDTO(price));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePrices(
            @PathVariable UUID id
    ) {
        deletePriceCasePort.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cinema/{cinemaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePricesByCinemaId(
            @PathVariable UUID cinemaId
    ) {
        deletePriceCasePort.deleteByCinemaId(cinemaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cinema/{cinemaId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> findPricesByCinemaId(
            @PathVariable UUID cinemaId
    ) {
        var price = findPriceCasePort.findByCinemaId(cinemaId);
        return ResponseEntity.ok(priceResponseMapper.toResponseDTO(price));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAllPrices(
            @RequestParam(defaultValue = "0") int page
    ) {
        var prices = findPriceCasePort.findAll(page);
        var response = priceResponseMapper.toResponseDTOPage(prices);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> findPricesById(
            @PathVariable UUID id
    ) {
        var price = findPriceCasePort.findById(id);
        return ResponseEntity.ok(price);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePrices(
            @PathVariable UUID id,
            @RequestBody UpdatePriceRequestDTO request
    ) {
        var price = updatePriceCasePort.update(request.toDTO(id));
        return ResponseEntity.ok(priceResponseMapper.toResponseDTO(price));
    }

}
