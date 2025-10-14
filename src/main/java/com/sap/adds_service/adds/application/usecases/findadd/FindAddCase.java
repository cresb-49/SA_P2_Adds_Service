package com.sap.adds_service.adds.application.usecases.findadd;

import com.sap.adds_service.adds.application.input.FindAddPort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.usecases.findadd.dtos.AddFilter;
import com.sap.adds_service.adds.domain.Add;
import com.sap.common_lib.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class FindAddCase implements FindAddPort {

    private final FindingAddPort findingAddPort;

    @Override
    public Add findById(UUID id) {
        return findingAddPort.findById(id).orElseThrow(
                () -> new NotFoundException("Add not found")
        );
    }

    @Override
    public Page<Add> findAll(int page) {
        return findingAddPort.findAll(page);
    }

    @Override
    public Page<Add> findByType(String type, int page) {
        return findingAddPort.findByType(type, page);
    }

    @Override
    public Page<Add> findByActive(boolean active, int page) {
        return findingAddPort.findByActive(active, page);
    }

    @Override
    public Page<Add> findByCinemaId(UUID cinemaId, int page) {
        return findingAddPort.findByCinemaId(cinemaId, page);
    }

    @Override
    public Page<Add> findByFilters(AddFilter filter, int page) {
        return findingAddPort.findByFilers(filter, page);
    }

    @Override
    public Page<Add> findByUserId(UUID userId, int page) {
        return findingAddPort.findByUserId(userId, page);
    }

    @Override
    public List<Add> findByIds(List<UUID> ids) {
        return findingAddPort.findByIds(ids);
    }
}
