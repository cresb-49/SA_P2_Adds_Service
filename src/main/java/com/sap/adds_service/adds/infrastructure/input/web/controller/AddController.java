package com.sap.adds_service.adds.infrastructure.input.web.controller;

import com.sap.adds_service.adds.application.input.*;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.adds_service.adds.infrastructure.input.web.dtos.AddFilterRequestDTO;
import com.sap.adds_service.adds.infrastructure.input.web.dtos.CreateAddRequestDTO;
import com.sap.adds_service.adds.infrastructure.input.web.dtos.UpdateAddRequestDTO;
import com.sap.adds_service.adds.infrastructure.input.web.mappers.AddResponseMapper;
import com.sap.common_lib.dto.response.RestApiErrorDTO;
import com.sap.common_lib.dto.response.add.events.ChangePaidStateAddEventDTO;
import com.sap.common_lib.events.topics.TopicConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Tag(name = "Anuncios", description = "Endpoints para gestionar anuncios (adds) del sistema")
@Controller
@RequestMapping("/api/v1/adds")
@AllArgsConstructor
public class AddController {

    private final KafkaTemplate<String, ChangePaidStateAddEventDTO> kafka;

    // Use case interfaces
    private final ChangeStateAddPort changeStateAddPort;
    private final CreateAddPort createAddPort;
    private final DeleteAddPort deleteAddPort;
    private final FindAddPort findAddPort;
    private final GetRandomAddPort getRandomAddPort;
    private final UpdateAddPort updateAddPort;
    private final RetryPaidAddCasePort retryPaidAddCasePort;
    private final BuyAddsReportCasePort buyAddsReportCasePort;
    // Mapper
    private final AddResponseMapper addResponseMapper;

    @Operation(
            summary = "Buscar anuncios por filtros",
            description = "Filtra anuncios por tipo, estado de pago, activo, cine y usuario. Retorna una página de resultados.")
    @Parameters({
            @Parameter(name = "type", description = "Tipo de anuncio", schema = @Schema(implementation = AddType.class), examples = {
                    @ExampleObject(name = "Texto", value = "TEXT_BANNER"),
                    @ExampleObject(name = "Vertical", value = "MEDIA_VERTICAL"),
                    @ExampleObject(name = "Horizontal", value = "MEDIA_HORIZONTAL")
            }),
            @Parameter(name = "paymentState", description = "Estado de pago del anuncio", schema = @Schema(implementation = PaymentState.class)),
            @Parameter(name = "active", description = "Si el anuncio está activo"),
            @Parameter(name = "cinemaId", description = "Identificador del cine"),
            @Parameter(name = "userId", description = "Identificador del usuario propietario"),
            @Parameter(name = "page", description = "Número de página (0-index)", example = "0")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de anuncios recuperada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAllAddsByFilters(
            @RequestParam(required = false) AddType type,
            @RequestParam(required = false) PaymentState paymentState,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(defaultValue = "0") int page
    ) {
        var filter = new AddFilterRequestDTO(type, paymentState, active, cinemaId, userId);
        var result = findAddPort.findByFilters(filter.toDomain(), page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @Operation(summary = "Listar todos los anuncios", description = "Retorna una página con todos los anuncios.")
    @Parameters({
            @Parameter(name = "page", description = "Número de página (0-index)", example = "0")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de anuncios recuperada correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAllAdds(
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findAll(page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @Operation(summary = "Obtener anuncio aleatorio público",
            description = "Obtiene un anuncio aleatorio activo por tipo y cine. Endpoint público.")
    @Parameters({
            @Parameter(name = "cinemaId", description = "Identificador del cine", required = true),
            @Parameter(name = "type", description = "Tipo de anuncio", required = true, schema = @Schema(implementation = AddType.class)),
            @Parameter(name = "currentDateTime", description = "Fecha y hora ISO-8601 opcional para evaluar vigencia", example = "2025-10-19T12:34:56")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Anuncio encontrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe un anuncio vigente que cumpla los criterios", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    //Public endpoint to get random add by type and cinema id
    @GetMapping("/public/cinema/{cinemaId}/type/{type}/random")
    public ResponseEntity<?> getRandomAdd(
            @PathVariable UUID cinemaId,
            @PathVariable AddType type,
            @RequestParam(required = false) String currentDateTime
    ) {
        var currentDateTimeParsed = currentDateTime != null ? LocalDateTime.parse(currentDateTime) : LocalDateTime.now();
        var add = getRandomAddPort.randomAddByTypeAndCinemaId(type, cinemaId, currentDateTimeParsed);
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

    @Operation(summary = "Obtener anuncio por ID", description = "Recupera la información de un anuncio por su identificador.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del anuncio", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Anuncio encontrado"),
            @ApiResponse(responseCode = "404", description = "Anuncio no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAddById(@PathVariable UUID id) {
        var add = findAddPort.findById(id);
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

    @Operation(summary = "Listar anuncios por tipo", description = "Retorna una página de anuncios filtrados por tipo.")
    @Parameters({
            @Parameter(name = "type", description = "Tipo de anuncio", required = true),
            @Parameter(name = "page", description = "Número de página (0-index)", example = "0")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de anuncios recuperada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAddsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findByType(type, page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @Operation(summary = "Listar anuncios por estado activo", description = "Retorna una página de anuncios según su estado de activación.")
    @Parameters({
            @Parameter(name = "active", description = "Si el anuncio está activo", required = true),
            @Parameter(name = "page", description = "Número de página (0-index)", example = "0")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de anuncios recuperada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/active/{active}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAddsByActive(
            @PathVariable boolean active,
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findByActive(active, page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @Operation(summary = "Listar anuncios por cine", description = "Retorna una página de anuncios pertenecientes a un cine específico.")
    @Parameters({
            @Parameter(name = "cinemaId", description = "Identificador del cine", required = true),
            @Parameter(name = "page", description = "Número de página (0-index)", example = "0")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de anuncios recuperada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/cinema/{cinemaId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAddsByCinemaId(
            @PathVariable UUID cinemaId,
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findByCinemaId(cinemaId, page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @Operation(summary = "Listar anuncios por usuario", description = "Retorna una página de anuncios creados por un usuario.")
    @Parameters({
            @Parameter(name = "userId", description = "Identificador del usuario", required = true),
            @Parameter(name = "page", description = "Número de página (0-index)", example = "0")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de anuncios recuperada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAddsByUserId(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findByUserId(userId, page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @Operation(summary = "Obtener anuncios por lista de IDs", description = "Recupera un listado de anuncios a partir de sus identificadores.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de anuncios recuperado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/ids")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAddsByIds(@RequestBody List<UUID> ids) {
        var adds = findAddPort.findByIds(ids);
        return ResponseEntity.ok(addResponseMapper.toResponseList(adds));
    }

    @Operation(summary = "Crear anuncio",
            description = "Crea un nuevo anuncio. Puede incluir un archivo multimedia opcional.")
    @Parameters({
            @Parameter(name = "file", description = "Archivo multimedia opcional (imagen o video)", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Anuncio creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto: el recurso ya existe", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> createAdd(
            @ModelAttribute CreateAddRequestDTO requestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        var add = createAddPort.create(requestDTO.toDomain(file));
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

    @Operation(summary = "Eliminar anuncio", description = "Elimina un anuncio por su identificador.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del anuncio", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Anuncio eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Anuncio no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> deleteAdd(@PathVariable UUID id) {
        deleteAddPort.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cambiar estado de activación", description = "Activa o desactiva un anuncio.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del anuncio", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Anuncio no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto de estado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/state/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> changeStateAdd(@PathVariable UUID id) {
        var add = changeStateAddPort.changeState(id);
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

    @Operation(summary = "Actualizar anuncio",
            description = "Actualiza los datos de un anuncio existente. Puede incluir un archivo multimedia opcional.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del anuncio", required = true),
            @Parameter(name = "file", description = "Archivo multimedia opcional (imagen o video)", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Anuncio actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Anuncio no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto de actualización", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> updateAdd(
            @PathVariable UUID id,
            @ModelAttribute UpdateAddRequestDTO updateAddRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        var add = updateAddPort.update(updateAddRequestDTO.toDomain(id, file));
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

    @Operation(summary = "Reintentar confirmación de pago",
            description = "Reintenta la lógica de confirmación/cambio de estado de pago para un anuncio específico.")
    @Parameters({
            @Parameter(name = "addId", description = "Identificador del anuncio", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Proceso de reintento ejecutado"),
            @ApiResponse(responseCode = "404", description = "Anuncio no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @PostMapping("/retry-paid/{addId}")
    public ResponseEntity<?> retryPaidAdd(@PathVariable UUID addId) {
        retryPaidAddCasePort.retryPaidAdd(addId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "(Demo) Probar envío de evento a Kafka",
            description = "Endpoint de prueba pública para enviar un evento de cambio de estado de pago a Kafka.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Evento enviado"),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/public/test/kafka")
    public ResponseEntity<?> testKafka() {

        var event = new ChangePaidStateAddEventDTO(
                UUID.fromString("4638bb22-eb97-404a-bfb3-a6a8359307b8"),
                true,
                "Pago realizado con exito"
        );
        kafka.send(TopicConstants.UPDATE_PAID_STATUS_ADD_TOPIC, event.addId().toString(), event);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Generar reporte de anuncios comprados",
            description = "Genera un reporte de anuncios comprados filtrado por tipo y rango de fechas.")
    @Parameters({
            @Parameter(name = "addType", description = "Tipo de anuncio (opcional)", schema = @Schema(implementation = AddType.class)),
            @Parameter(name = "initialDate", description = "Fecha inicial en formato ISO (yyyy-MM-dd) (opcional)", example = "2025-01-01"),
            @Parameter(name = "finalDate", description = "Fecha final en formato ISO (yyyy-MM-dd) (opcional)", example = "2025-01-31")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @PostMapping("/report/bought")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reportBoughtAdds(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String addType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo
    ) {
        var result = buyAddsReportCasePort.report(
                from,
                to,
                addType,
                periodFrom,
                periodTo
        );
        return ResponseEntity.ok(addResponseMapper.toResponseList(result));
    }

    @Operation(
            summary = "Generar reporte de anuncios comprados (PDF)",
            description = "Genera y descarga en formato PDF un reporte de anuncios comprados filtrado por tipo y rango de fechas.")
    @Parameters({
            @Parameter(name = "from", description = "Fecha y hora inicial en formato ISO-8601 (yyyy-MM-dd'T'HH:mm:ss)", example = "2025-01-01T00:00:00"),
            @Parameter(name = "to", description = "Fecha y hora final en formato ISO-8601 (yyyy-MM-dd'T'HH:mm:ss)", example = "2025-01-31T23:59:59"),
            @Parameter(name = "addType", description = "Tipo de anuncio (opcional)", schema = @Schema(implementation = AddType.class)),
            @Parameter(name = "periodFrom", description = "Fecha inicial del periodo en formato ISO (yyyy-MM-dd) (opcional)", example = "2025-01-01"),
            @Parameter(name = "periodTo", description = "Fecha final del periodo en formato ISO (yyyy-MM-dd) (opcional)", example = "2025-01-31")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte PDF generado correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @PostMapping("/report/bought/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> reportBoughtAddsPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String addType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo
    ) {
        var pdf = buyAddsReportCasePort.generateReportFile(
                from,
                to,
                addType,
                periodFrom,
                periodTo,
                "PDF"
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ads_purchased_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
