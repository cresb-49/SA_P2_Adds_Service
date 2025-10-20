

package com.sap.adds_service.duration.infrastructure.output.jpa.adapter;

import com.sap.adds_service.duration.application.output.DeleteDurationPort;
import com.sap.adds_service.duration.application.output.FindDurationPort;
import com.sap.adds_service.duration.application.output.SaveDurationPort;
import com.sap.adds_service.duration.domain.Duration;
import com.sap.adds_service.duration.infrastructure.output.jpa.entity.DurationEntity;
import com.sap.adds_service.duration.infrastructure.output.jpa.mapper.DurationMapper;
import com.sap.adds_service.duration.infrastructure.output.jpa.repository.DurationEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DurationJpaAdapterTest {

    @Mock
    private DurationEntityRepository repository;

    @Mock
    private DurationMapper mapper;

    @InjectMocks
    private DurationJpaAdapter adapter;

    private static final UUID ID = UUID.randomUUID();
    private static final int DAYS = 7;

    private DurationEntity entity;
    private Duration domain;

    @BeforeEach
    void setup() {
        entity = new DurationEntity();
        domain = new Duration(ID, DAYS, LocalDateTime.now());
    }

    @Test
    void deleteById_shouldDelegateToRepository() {
        // Arrange
        doNothing().when(repository).deleteById(ID);
        // Act
        adapter.deleteById(ID);
        // Assert
        verify(repository).deleteById(ID);
    }

    @Test
    void findById_shouldReturnMapped_whenFound() {
        // Arrange
        when(repository.findById(ID)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);
        // Act
        Optional<Duration> result = adapter.findById(ID);
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        // Arrange
        when(repository.findById(ID)).thenReturn(Optional.empty());
        // Act
        Optional<Duration> result = adapter.findById(ID);
        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldMapAllEntities() {
        // Arrange
        var e1 = new DurationEntity();
        var e2 = new DurationEntity();
        var d1 = new Duration(UUID.randomUUID(), 3, LocalDateTime.now());
        var d2 = new Duration(UUID.randomUUID(), 10, LocalDateTime.now());
        when(repository.findAll()).thenReturn(List.of(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(d1);
        when(mapper.toDomain(e2)).thenReturn(d2);
        // Act
        List<Duration> result = adapter.findAll();
        // Assert
        assertThat(result).containsExactly(d1, d2);
    }

    @Test
    void findByIds_shouldMapAllFoundEntities() {
        // Arrange
        var ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        var e1 = new DurationEntity();
        var e2 = new DurationEntity();
        var d1 = new Duration(ids.get(0), 5, LocalDateTime.now());
        var d2 = new Duration(ids.get(1), 8, LocalDateTime.now());
        when(repository.findAllById(ids)).thenReturn(List.of(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(d1);
        when(mapper.toDomain(e2)).thenReturn(d2);
        // Act
        List<Duration> result = adapter.findByIds(ids);
        // Assert
        assertThat(result).containsExactly(d1, d2);
    }

    @Test
    void existsByDaysDuration_shouldDelegateToRepository() {
        // Arrange
        when(repository.existsByDays(DAYS)).thenReturn(true);
        // Act
        boolean result = adapter.existsByDaysDuration(DAYS);
        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void save_shouldMapToEntity_andBackToDomain() {
        // Arrange
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);
        // Act
        Duration result = adapter.save(domain);
        // Assert
        assertThat(result).isEqualTo(domain);
        verify(mapper).toEntity(domain);
        verify(repository).save(entity);
        verify(mapper).toDomain(entity);
    }
}