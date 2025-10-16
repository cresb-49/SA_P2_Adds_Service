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

@RestController
@RequestMapping("/api/v1/durations")
@AllArgsConstructor
public class DurationController {
    private final CreateDurationCasePort createDurationCasePort;
    private final DeleteDurationCasePort deleteDurationCasePort;
    private final FindDurationCasePort findDurationCasePort;

    private final DurationResponseMapper durationResponseMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDuration(@RequestBody CreateDurationRequestDTO createDurationRequestDTO) {
        var duration = createDurationCasePort.create(createDurationRequestDTO.toDTO());
        var response = durationResponseMapper.toResponseDTO(duration);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findDurationById(@PathVariable UUID id) {
        var duration = findDurationCasePort.findById(id);
        var response = durationResponseMapper.toResponseDTO(duration);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDurationById(@PathVariable UUID id) {
        deleteDurationCasePort.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAllDurations() {
        var durations = findDurationCasePort.findAll();
        var response = durationResponseMapper.toResponseDTOList(durations);
        return ResponseEntity.ok(response);
    }

}
