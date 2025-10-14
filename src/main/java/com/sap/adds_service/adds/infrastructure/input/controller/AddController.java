package com.sap.adds_service.adds.infrastructure.input.controller;

import com.sap.adds_service.adds.application.input.*;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.infrastructure.input.dtos.AddFilterRequestDTO;
import com.sap.adds_service.adds.infrastructure.input.dtos.CreateAddRequestDTO;
import com.sap.adds_service.adds.infrastructure.input.dtos.UpdateAddRequestDTO;
import com.sap.adds_service.adds.infrastructure.input.mappers.AddResponseMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/adds")
@AllArgsConstructor
public class AddController {

    // Use case interfaces
    private final ChangeStateAddPort changeStateAddPort;
    private final CreateAddPort createAddPort;
    private final DeleteAddPort deleteAddPort;
    private final FindAddPort findAddPort;
    private final GetRandomAddPort getRandomAddPort;
    private final UpdateAddPort updateAddPort;
    // Mapper
    private final AddResponseMapper addResponseMapper;

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAllAddsByFilters(
            @RequestParam(required = false) AddType type,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(defaultValue = "0") int page
    ) {
        var filter = new AddFilterRequestDTO(type, active, cinemaId, userId);
        var result = findAddPort.findByFilters(filter.toDomain(), page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAllAdds(
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findAll(page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    //Public endpoint to get random add by type and cinema id
    @GetMapping("/cinema/{cinemaId}/type/{type}/random")
    public ResponseEntity<?> getRandomAdd(
            @PathVariable UUID cinemaId,
            @PathVariable String type,
            @RequestParam(required = false) String currentDateTime
    ) {
        var currentDateTimeParsed = currentDateTime != null ? LocalDateTime.parse(currentDateTime) : LocalDateTime.now();
        var add = getRandomAddPort.randomAddByTypeAndCinemaId(type, cinemaId, currentDateTimeParsed);
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAddById(@PathVariable UUID id) {
        var add = findAddPort.findById(id);
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAddsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findByType(type, page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @GetMapping("/active/{active}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAddsByActive(
            @PathVariable boolean active,
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findByActive(active, page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @GetMapping("/cinema/{cinemaId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAddsByCinemaId(
            @PathVariable UUID cinemaId,
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findByCinemaId(cinemaId, page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAddsByUserId(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findByUserId(userId, page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @PostMapping("/ids")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> getAddsByIds(@RequestBody List<UUID> ids) {
        var adds = findAddPort.findByIds(ids);
        return ResponseEntity.ok(addResponseMapper.toResponseList(adds));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> createAdd(
            @ModelAttribute CreateAddRequestDTO requestDTO,
            @RequestPart("file") MultipartFile file
    ) {
        var add = createAddPort.create(requestDTO.toDomain(file));
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> deleteAdd(@PathVariable UUID id) {
        deleteAddPort.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/state/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('SPONSOR')")
    public ResponseEntity<?> changeStateAdd(@PathVariable UUID id) {
        var add = changeStateAddPort.changeState(id);
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

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

}
