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
import com.sap.common_lib.dto.response.RestApiErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Precios", description = "Endpoints para gestionar precios de anuncios por cine")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/prices")
@AllArgsConstructor
public class PriceController {

    private final CreatePriceCasePort createPriceCasePort;
    private final DeletePriceCasePort deletePriceCasePort;
    private final FindPriceCasePort findPriceCasePort;
    private final UpdatePriceCasePort updatePriceCasePort;

    private final PriceResponseMapper priceResponseMapper;

    @Operation(summary = "Crear precios por cine", description = "Genera el conjunto de precios por defecto para el cine indicado.")
    @Parameters({
            @Parameter(name = "cinemaId", description = "Identificador del cine", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Precios creados correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cine no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto: ya existen precios para el cine", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @PostMapping("/cinema/{cinemaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPrices(
            @PathVariable UUID cinemaId
    ) {
        var price = createPriceCasePort.createPrices(cinemaId);
        return ResponseEntity.ok(priceResponseMapper.toResponseDTO(price));
    }

    @Operation(summary = "Eliminar precios por ID", description = "Elimina el registro de precios por su identificador.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador de los precios", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePrices(
            @PathVariable UUID id
    ) {
        deletePriceCasePort.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar precios por cine", description = "Elimina el registro de precios asociado a un cine.")
    @Parameters({
            @Parameter(name = "cinemaId", description = "Identificador del cine", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @DeleteMapping("/cinema/{cinemaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePricesByCinemaId(
            @PathVariable UUID cinemaId
    ) {
        deletePriceCasePort.deleteByCinemaId(cinemaId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener precios por cine", description = "Recupera el registro de precios asociado a un cine.")
    @Parameters({
            @Parameter(name = "cinemaId", description = "Identificador del cine", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Precios recuperados correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/cinema/{cinemaId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> findPricesByCinemaId(
            @PathVariable UUID cinemaId
    ) {
        var price = findPriceCasePort.findByCinemaId(cinemaId);
        return ResponseEntity.ok(priceResponseMapper.toResponseDTO(price));
    }

    @Operation(summary = "Listar todos los precios", description = "Retorna una página con todos los registros de precios.")
    @Parameters({
            @Parameter(name = "page", description = "Número de página (0-index)", example = "0")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de precios recuperada correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAllPrices(
            @RequestParam(defaultValue = "0") int page
    ) {
        var prices = findPriceCasePort.findAll(page);
        var response = priceResponseMapper.toResponseDTOPage(prices);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Obtener precios por ID", description = "Recupera el registro de precios por su identificador.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador de los precios", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Precios recuperados correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> findPricesById(
            @PathVariable UUID id
    ) {
        var price = findPriceCasePort.findById(id);
        return ResponseEntity.ok(price);
    }

    @Operation(summary = "Actualizar precios", description = "Actualiza los valores de precios para el registro indicado.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador de los precios", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Precios actualizados correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto en la actualización", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
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
