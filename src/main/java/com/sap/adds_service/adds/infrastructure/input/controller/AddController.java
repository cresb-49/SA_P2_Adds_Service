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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping
    public ResponseEntity<?> getAllAddsByFilters(
            @RequestParam(required = false) AddType type,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(defaultValue = "0") int page
    ) {
        var filter = new AddFilterRequestDTO(type, active, cinemaId);
        var result = findAddPort.findByFilters(filter.toDomain(), page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAdds(
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findAll(page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @GetMapping("/cinema/{cinemaId}/type/{type}/random")
    public ResponseEntity<?> getRandomAdd(
            @RequestParam UUID cinemaId,
            @RequestParam String type
    ) {
        var add = getRandomAddPort.randomAddByTypeAndCinemaId(type, cinemaId);
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAddById(@RequestParam UUID id) {
        var add = findAddPort.findById(id);
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getAddsByType(
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findByType(type, page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @GetMapping("/active/{active}")
    public ResponseEntity<?> getAddsByActive(
            @RequestParam boolean active,
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findByActive(active, page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @GetMapping("/cinema/{cinemaId}")
    public ResponseEntity<?> getAddsByCinemaId(
            @RequestParam UUID cinemaId,
            @RequestParam(defaultValue = "0") int page
    ) {
        var result = findAddPort.findByCinemaId(cinemaId, page);
        return ResponseEntity.ok(addResponseMapper.toResponsePage(result));
    }

    @PostMapping("/ids")
    public ResponseEntity<?> getAddsByIds(@RequestParam List<UUID> ids) {
        var adds = findAddPort.findByIds(ids);
        return ResponseEntity.ok(addResponseMapper.toResponseList(adds));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAdd(
            @ModelAttribute CreateAddRequestDTO requestDTO,
            @RequestPart("file") MultipartFile file
    ) {
        var add = createAddPort.create(requestDTO.toDomain(file));
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdd(@RequestParam UUID id) {
        deleteAddPort.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/state/{id}")
    public ResponseEntity<?> changeStateAdd(@RequestParam UUID id) {
        var add = changeStateAddPort.changeState(id);
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAdd(
            @RequestParam UUID id,
            @ModelAttribute UpdateAddRequestDTO updateAddRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        var add = updateAddPort.update(updateAddRequestDTO.toDomain(id, file));
        return ResponseEntity.ok(addResponseMapper.toResponse(add));
    }

}
