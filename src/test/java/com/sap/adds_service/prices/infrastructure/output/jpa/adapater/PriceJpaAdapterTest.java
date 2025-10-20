

package com.sap.adds_service.prices.infrastructure.output.jpa.adapater;

import com.sap.adds_service.prices.domain.Price;
import com.sap.adds_service.prices.infrastructure.output.jpa.entity.PriceEntity;
import com.sap.adds_service.prices.infrastructure.output.jpa.mapper.PriceMapper;
import com.sap.adds_service.prices.infrastructure.output.jpa.repository.PriceEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceJpaAdapterTest {

    @Mock
    private PriceEntityRepository repository;

    @Mock
    private PriceMapper mapper;

    @InjectMocks
    private PriceJpaAdapter adapter;

    private static final UUID ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();

    @Test
    void findById_shouldReturnMapped_whenFound() {
        // Arrange
        PriceEntity entity = new PriceEntity();
        Price domain = new Price(CINEMA_ID, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN);
        when(repository.findById(ID)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);
        // Act
        Optional<Price> result = adapter.findById(ID);
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);
        verify(repository).findById(ID);
        verify(mapper).toDomain(entity);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        // Arrange
        when(repository.findById(ID)).thenReturn(Optional.empty());
        // Act
        Optional<Price> result = adapter.findById(ID);
        // Assert
        assertThat(result).isEmpty();
        verify(repository).findById(ID);
        verifyNoInteractions(mapper);
    }

    @Test
    void checkIfCinemaExistsById_shouldDelegate() {
        // Arrange
        when(repository.existsByCinemaId(CINEMA_ID)).thenReturn(true);
        // Act
        boolean result = adapter.checkIfCinemaExistsById(CINEMA_ID);
        // Assert
        assertThat(result).isTrue();
        verify(repository).existsByCinemaId(CINEMA_ID);
    }

    @Test
    void findByCinemaId_shouldReturnMapped_whenFound() {
        // Arrange
        PriceEntity entity = new PriceEntity();
        Price domain = new Price(CINEMA_ID, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
        when(repository.findByCinemaId(CINEMA_ID)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);
        // Act
        Optional<Price> result = adapter.findByCinemaId(CINEMA_ID);
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);
        verify(repository).findByCinemaId(CINEMA_ID);
        verify(mapper).toDomain(entity);
    }

    @Test
    void findByCinemaId_shouldReturnEmpty_whenNotFound() {
        // Arrange
        when(repository.findByCinemaId(CINEMA_ID)).thenReturn(Optional.empty());
        // Act
        Optional<Price> result = adapter.findByCinemaId(CINEMA_ID);
        // Assert
        assertThat(result).isEmpty();
        verify(repository).findByCinemaId(CINEMA_ID);
        verifyNoInteractions(mapper);
    }

    @Test
    void findAll_shouldMapPage() {
        // Arrange
        PriceEntity e1 = new PriceEntity();
        PriceEntity e2 = new PriceEntity();
        Price d1 = new Price(CINEMA_ID, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
        Price d2 = new Price(UUID.randomUUID(), BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN);
        Page<PriceEntity> page = new PageImpl<>(List.of(e1, e2), PageRequest.of(0, 20), 2);
        when(repository.findAll(PageRequest.of(0, 20))).thenReturn(page);
        when(mapper.toDomain(e1)).thenReturn(d1);
        when(mapper.toDomain(e2)).thenReturn(d2);
        // Act
        Page<Price> result = adapter.findAll(0);
        // Assert
        assertThat(result.getContent()).containsExactly(d1, d2);
        verify(repository).findAll(PageRequest.of(0, 20));
        verify(mapper).toDomain(e1);
        verify(mapper).toDomain(e2);
    }

    @Test
    void save_shouldMapToEntity_andBackToDomain() {
        // Arrange
        Price domain = new Price(CINEMA_ID, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
        PriceEntity entity = new PriceEntity();
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);
        // Act
        Price saved = adapter.save(domain);
        // Assert
        assertThat(saved).isEqualTo(domain);
        verify(mapper).toEntity(domain);
        verify(repository).save(entity);
        verify(mapper).toDomain(entity);
    }

    @Test
    void deleteByCinemaId_shouldDelegate() {
        // Arrange
        doNothing().when(repository).deleteByCinemaId(CINEMA_ID);
        // Act
        adapter.deleteByCinemaId(CINEMA_ID);
        // Assert
        verify(repository).deleteByCinemaId(CINEMA_ID);
    }

    @Test
    void deleteById_shouldDelegate() {
        // Arrange
        doNothing().when(repository).deleteById(ID);
        // Act
        adapter.deleteById(ID);
        // Assert
        verify(repository).deleteById(ID);
    }
}