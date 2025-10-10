package com.sap.adds_service.adds.infrastructure.output.jpa.adapter;

import com.sap.adds_service.adds.application.usecases.findadd.dtos.AddFilter;
import com.sap.adds_service.adds.application.output.DeletingAddPort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.infrastructure.output.jpa.mapper.AddMapper;
import com.sap.adds_service.adds.infrastructure.output.jpa.repository.AddEntityRepository;
import com.sap.adds_service.adds.infrastructure.output.jpa.specifications.AddEntitySpecs;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class AddJpaAdapter implements FindingAddPort, SaveAddPort, DeletingAddPort {

    private final AddEntityRepository addEntityRepository;
    private final AddMapper addMapper;

    @Override
    public void deleteById(UUID id) {
        addEntityRepository.deleteById(id);
    }

    @Override
    public Optional<Add> findById(UUID id) {
        return addEntityRepository.findById(id).map(addMapper::toDomain);
    }

    @Override
    public Page<Add> findAll(int page) {
        var result = addEntityRepository.findAll(
                PageRequest.of(page, 20)
        );
        return result.map(addMapper::toDomain);
    }

    @Override
    public Page<Add> findByType(String type, int page) {
        var result = addEntityRepository.findByType(
                type,
                PageRequest.of(page, 20)
        );
        return result.map(addMapper::toDomain);
    }

    @Override
    public Page<Add> findByCinemaId(UUID cinemaId, int page) {
        var result = addEntityRepository.findByCinemaId(
                cinemaId,
                PageRequest.of(page, 20)
        );
        return result.map(addMapper::toDomain);
    }

    @Override
    public Page<Add> findByActive(boolean active, int page) {
        var result = addEntityRepository.findByActive(
                active,
                PageRequest.of(page, 20)
        );
        return result.map(addMapper::toDomain);
    }

    @Override
    public List<Add> findByIds(List<UUID> ids) {
        return addEntityRepository.findByIdIn(ids).stream().map(addMapper::toDomain).toList();
    }

    @Override
    public Page<Add> findByFilers(AddFilter filter, int page) {
        var spec = AddEntitySpecs.byFilter(filter);
        var result = addEntityRepository.findAll(spec, PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        return result.map(addMapper::toDomain);
    }

    @Override
    public Add save(Add add) {
        var entity = addMapper.toEntity(add);
        var savedEntity = addEntityRepository.save(entity);
        return addMapper.toDomain(savedEntity);
    }
}
