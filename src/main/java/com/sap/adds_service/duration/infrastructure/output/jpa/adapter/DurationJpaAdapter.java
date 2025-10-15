package com.sap.adds_service.duration.infrastructure.output.jpa.adapter;

import com.sap.adds_service.duration.application.output.DeleteDurationPort;
import com.sap.adds_service.duration.application.output.FindDurationPort;
import com.sap.adds_service.duration.application.output.SaveDurationPort;
import com.sap.adds_service.duration.domain.Duration;
import com.sap.adds_service.duration.infrastructure.output.jpa.mapper.DurationMapper;
import com.sap.adds_service.duration.infrastructure.output.jpa.repository.DurationEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class DurationJpaAdapter implements DeleteDurationPort, FindDurationPort, SaveDurationPort {

    private final DurationEntityRepository durationEntityRepository;
    private final DurationMapper durationMapper;

    @Override
    public void deleteById(UUID id) {
        durationEntityRepository.deleteById(id);
    }

    @Override
    public Optional<Duration> findById(UUID id) {
        return durationEntityRepository.findById(id)
                .map(durationMapper::toDomain);
    }

    @Override
    public List<Duration> findAll() {
        return durationEntityRepository.findAll()
                .stream()
                .map(durationMapper::toDomain)
                .toList();
    }

    @Override
    public List<Duration> findByIds(List<UUID> ids) {
        return durationEntityRepository.findAllById(ids)
                .stream()
                .map(durationMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByDaysDuration(int daysDuration) {
        return durationEntityRepository.existsByDays(daysDuration);
    }

    @Override
    public Duration save(Duration duration) {
        var entity = durationMapper.toEntity(duration);
        var savedEntity = durationEntityRepository.save(entity);
        return durationMapper.toDomain(savedEntity);
    }
}
