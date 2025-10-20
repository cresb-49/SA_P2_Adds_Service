package com.sap.adds_service.duration.infrastructure.input.web.controller;

import com.sap.adds_service.duration.application.input.CreateDurationCasePort;
import com.sap.adds_service.duration.application.input.DeleteDurationCasePort;
import com.sap.adds_service.duration.application.input.FindDurationCasePort;
import com.sap.adds_service.duration.infrastructure.input.web.dtos.CreateDurationRequestDTO;
import com.sap.adds_service.duration.infrastructure.input.web.mapper.DurationResponseMapper;
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
@Tag(name = "Duraciones", description = "Endpoints para gestionar duraciones de anuncios")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/durations")
@AllArgsConstructor
public class DurationController {
    private final CreateDurationCasePort createDurationCasePort;
    private final DeleteDurationCasePort deleteDurationCasePort;
    private final FindDurationCasePort findDurationCasePort;

    private final DurationResponseMapper durationResponseMapper;

    @Operation(summary = "Crear duración", description = "Crea un nuevo registro de duración para anuncios.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Duración creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto: el recurso ya existe", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDuration(@RequestBody CreateDurationRequestDTO createDurationRequestDTO) {
        var duration = createDurationCasePort.create(createDurationRequestDTO.toDTO());
        var response = durationResponseMapper.toResponseDTO(duration);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener duración por ID", description = "Recupera la información de una duración por su identificador.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador de la duración", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Duración encontrada"),
            @ApiResponse(responseCode = "404", description = "Duración no encontrada", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findDurationById(@PathVariable UUID id) {
        var duration = findDurationCasePort.findById(id);
        var response = durationResponseMapper.toResponseDTO(duration);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar duración", description = "Elimina un registro de duración por su identificador.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador de la duración", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Duración eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Duración no encontrada", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDurationById(@PathVariable UUID id) {
        deleteDurationCasePort.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todas las duraciones", description = "Retorna el listado completo de duraciones registradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado recuperado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAllDurations() {
        var durations = findDurationCasePort.findAll();
        var response = durationResponseMapper.toResponseDTOList(durations);
        return ResponseEntity.ok(response);
    }

}
