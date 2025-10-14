package com.sap.adds_service.adds.application.input;

import com.sap.adds_service.adds.application.usecases.findadd.dtos.AddFilter;
import com.sap.adds_service.adds.domain.Add;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface FindAddPort {
    Add findById(UUID id);
    Page<Add> findAll(int page);
    Page<Add> findByType(String type, int page);
    Page<Add> findByActive(boolean active, int page);
    Page<Add> findByCinemaId(UUID cinemaId, int page);
    Page<Add> findByFilters(AddFilter filter, int page);
    Page<Add> findByUserId(UUID userId, int page);
    List<Add> findByIds(List<UUID> ids);
}
