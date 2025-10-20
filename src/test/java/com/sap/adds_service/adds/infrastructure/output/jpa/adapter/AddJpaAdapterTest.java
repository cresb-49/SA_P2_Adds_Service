package com.sap.adds_service.adds.infrastructure.output.jpa.adapter;

import com.sap.adds_service.adds.application.usecases.findadd.dtos.AddFilter;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.adds_service.adds.infrastructure.output.jpa.entity.AddEntity;
import com.sap.adds_service.adds.infrastructure.output.jpa.mapper.AddMapper;
import com.sap.adds_service.adds.infrastructure.output.jpa.repository.AddEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddJpaAdapterTest {

    @Mock
    private AddEntityRepository addEntityRepository;

    @Mock
    private AddMapper addMapper;

    @InjectMocks
    private AddJpaAdapter adapter;

    private static final UUID ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    private AddEntity entity;
    private Add domain;
    private Page<AddEntity> entityPage;

    @BeforeEach
    void setup() {
        entity = new AddEntity();
        domain = mock(Add.class);
        entityPage = new PageImpl<>(List.of(entity));
    }

    @Test
    void deleteById_shouldCallRepositoryDelete() {
        // Arrange
        doNothing().when(addEntityRepository).deleteById(ID);

        // Act
        adapter.deleteById(ID);

        // Assert
        verify(addEntityRepository).deleteById(ID);
    }

    @Test
    void findById_shouldReturnMappedAdd_whenFound() {
        // Arrange
        when(addEntityRepository.findById(ID)).thenReturn(Optional.of(entity));
        when(addMapper.toDomain(entity)).thenReturn(domain);

        // Act
        Optional<Add> result = adapter.findById(ID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        // Arrange
        when(addEntityRepository.findById(ID)).thenReturn(Optional.empty());

        // Act
        Optional<Add> result = adapter.findById(ID);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldMapPageResult() {
        // Arrange
        when(addEntityRepository.findAll(PageRequest.of(0, 20))).thenReturn(entityPage);
        when(addMapper.toDomain(entity)).thenReturn(domain);

        // Act
        Page<Add> result = adapter.findAll(0);

        // Assert
        assertThat(result.getContent()).contains(domain);
    }

    @Test
    void findByType_shouldMapPageResult() {
        // Arrange
        when(addEntityRepository.findByType(AddType.MEDIA_VERTICAL, PageRequest.of(0, 20))).thenReturn(entityPage);
        when(addMapper.toDomain(entity)).thenReturn(domain);

        // Act
        Page<Add> result = adapter.findByType("MEDIA_VERTICAL", 0);

        // Assert
        assertThat(result.getContent()).contains(domain);
    }

    @Test
    void findByCinemaId_shouldMapPageResult() {
        // Arrange
        when(addEntityRepository.findByCinemaId(CINEMA_ID, PageRequest.of(0, 20))).thenReturn(entityPage);
        when(addMapper.toDomain(entity)).thenReturn(domain);

        // Act
        Page<Add> result = adapter.findByCinemaId(CINEMA_ID, 0);

        // Assert
        assertThat(result.getContent()).contains(domain);
    }

    @Test
    void findByUserId_shouldMapPageResult() {
        // Arrange
        when(addEntityRepository.findByUserId(USER_ID, PageRequest.of(0, 20))).thenReturn(entityPage);
        when(addMapper.toDomain(entity)).thenReturn(domain);

        // Act
        Page<Add> result = adapter.findByUserId(USER_ID, 0);

        // Assert
        assertThat(result.getContent()).contains(domain);
    }

    @Test
    void findByActive_shouldMapPageResult() {
        // Arrange
        when(addEntityRepository.findByActive(true, PageRequest.of(0, 20))).thenReturn(entityPage);
        when(addMapper.toDomain(entity)).thenReturn(domain);

        // Act
        Page<Add> result = adapter.findByActive(true, 0);

        // Assert
        assertThat(result.getContent()).contains(domain);
    }

    @Test
    void findByIds_shouldReturnMappedList() {
        // Arrange
        List<UUID> ids = List.of(ID);
        when(addEntityRepository.findByIdIn(ids)).thenReturn(List.of(entity));
        when(addMapper.toDomain(entity)).thenReturn(domain);

        // Act
        List<Add> result = adapter.findByIds(ids);

        // Assert
        assertThat(result).contains(domain);
    }

    @Test
    void findByFilers_shouldMapSpecificationResults() {
        // Arrange
        AddFilter filter = new AddFilter(AddType.TEXT_BANNER, PaymentState.PENDING, true, CINEMA_ID, USER_ID);
        when(addEntityRepository.findAll(any(Specification.class), eq(PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")))))
                .thenReturn(entityPage);
        when(addMapper.toDomain(entity)).thenReturn(domain);

        // Act
        Page<Add> result = adapter.findByFilers(filter, 0);

        // Assert
        assertThat(result.getContent()).contains(domain);
    }

    @Test
    void findAddRandomByTypeAndCinemaId_shouldReturnMappedAdd_whenFound() {
        // Arrange
        when(addEntityRepository.findRandomByTypeCinemaAndPaymentState("MEDIA_HORIZONTAL", CINEMA_ID, "COMPLETED"))
                .thenReturn(Optional.of(entity));
        when(addMapper.toDomain(entity)).thenReturn(domain);

        // Act
        Optional<Add> result = adapter.findAddRandomByTypeAndCinemaId("MEDIA_HORIZONTAL", CINEMA_ID, "COMPLETED");

        // Assert
        assertThat(result).isPresent().contains(domain);
    }

    @Test
    void findAddRandomByTypeAndCinemaId_shouldReturnEmpty_whenNotFound() {
        // Arrange
        when(addEntityRepository.findRandomByTypeCinemaAndPaymentState("MEDIA_HORIZONTAL", CINEMA_ID, "COMPLETED"))
                .thenReturn(Optional.empty());

        // Act
        Optional<Add> result = adapter.findAddRandomByTypeAndCinemaId("MEDIA_HORIZONTAL", CINEMA_ID, "COMPLETED");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findAddRandomByTypeAndCinemaIdAndNow_shouldReturnMappedAdd_whenFound() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        when(addEntityRepository.findRandomValidByTypeCinemaPaymentStateAndNow("MEDIA_VERTICAL", CINEMA_ID, "COMPLETED", now))
                .thenReturn(Optional.of(entity));
        when(addMapper.toDomain(entity)).thenReturn(domain);

        // Act
        Optional<Add> result = adapter.findAddRandomByTypeAndCinemaIdAndNow("MEDIA_VERTICAL", CINEMA_ID, "COMPLETED", now);

        // Assert
        assertThat(result).isPresent().contains(domain);
    }

    @Test
    void save_shouldMapAndReturnSavedAdd() {
        // Arrange
        when(addMapper.toEntity(domain)).thenReturn(entity);
        when(addEntityRepository.save(entity)).thenReturn(entity);
        when(addMapper.toDomain(entity)).thenReturn(domain);

        // Act
        Add result = adapter.save(domain);

        // Assert
        assertThat(result).isEqualTo(domain);
    }
}