

package com.sap.adds_service.adds.application.usecases.findadd;

import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.usecases.findadd.dtos.AddFilter;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.common_lib.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAddCaseTest {

    @Mock
    private FindingAddPort findingAddPort;

    @InjectMocks
    private FindAddCase useCase;

    private static final UUID ADD_ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    @DisplayName("findById: retorna Add cuando existe")
    void findById_shouldReturn_whenExists() {
        // Arrange
        Add add = sampleAdd();
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.of(add));
        // Act
        Add result = useCase.findById(ADD_ID);
        // Assert
        assertThat(result).isEqualTo(add);
        verify(findingAddPort, times(1)).findById(ADD_ID);
    }

    @Test
    @DisplayName("findById: lanza NotFoundException cuando no existe")
    void findById_shouldThrow_whenNotFound() {
        // Arrange
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> useCase.findById(ADD_ID)).isInstanceOf(NotFoundException.class);
        verify(findingAddPort, times(1)).findById(ADD_ID);
    }

    @Test
    @DisplayName("findAll: delega en el puerto y retorna Page")
    void findAll_shouldDelegate() {
        // Arrange
        Page<Add> page = new PageImpl<>(List.of(sampleAdd()));
        when(findingAddPort.findAll(0)).thenReturn(page);
        // Act
        Page<Add> result = useCase.findAll(0);
        // Assert
        assertThat(result).isEqualTo(page);
        verify(findingAddPort, times(1)).findAll(0);
    }

    @Test
    @DisplayName("findByType: delega y retorna Page")
    void findByType_shouldDelegate() {
        // Arrange
        String type = "MEDIA_HORIZONTAL";
        Page<Add> page = new PageImpl<>(List.of(sampleAdd()));
        when(findingAddPort.findByType(type, 1)).thenReturn(page);
        // Act
        Page<Add> result = useCase.findByType(type, 1);
        // Assert
        assertThat(result).isEqualTo(page);
        verify(findingAddPort, times(1)).findByType(type, 1);
    }

    @Test
    @DisplayName("findByActive: delega y retorna Page")
    void findByActive_shouldDelegate() {
        // Arrange
        Page<Add> page = new PageImpl<>(List.of(sampleAdd()));
        when(findingAddPort.findByActive(true, 2)).thenReturn(page);
        // Act
        Page<Add> result = useCase.findByActive(true, 2);
        // Assert
        assertThat(result).isEqualTo(page);
        verify(findingAddPort, times(1)).findByActive(true, 2);
    }

    @Test
    @DisplayName("findByCinemaId: delega y retorna Page")
    void findByCinemaId_shouldDelegate() {
        // Arrange
        Page<Add> page = new PageImpl<>(List.of(sampleAdd()));
        when(findingAddPort.findByCinemaId(CINEMA_ID, 3)).thenReturn(page);
        // Act
        Page<Add> result = useCase.findByCinemaId(CINEMA_ID, 3);
        // Assert
        assertThat(result).isEqualTo(page);
        verify(findingAddPort, times(1)).findByCinemaId(CINEMA_ID, 3);
    }

    @Test
    @DisplayName("findByFilters: delega y retorna Page")
    void findByFilters_shouldDelegate() {
        // Arrange
        AddFilter filter = new AddFilter(null, null, null, null, null);
        Page<Add> page = new PageImpl<>(List.of(sampleAdd()));
        when(findingAddPort.findByFilers(filter, 4)).thenReturn(page);
        // Act
        Page<Add> result = useCase.findByFilters(filter, 4);
        // Assert
        assertThat(result).isEqualTo(page);
        verify(findingAddPort, times(1)).findByFilers(filter, 4);
    }

    @Test
    @DisplayName("findByUserId: delega y retorna Page")
    void findByUserId_shouldDelegate() {
        // Arrange
        Page<Add> page = new PageImpl<>(List.of(sampleAdd()));
        when(findingAddPort.findByUserId(USER_ID, 5)).thenReturn(page);
        // Act
        Page<Add> result = useCase.findByUserId(USER_ID, 5);
        // Assert
        assertThat(result).isEqualTo(page);
        verify(findingAddPort, times(1)).findByUserId(USER_ID, 5);
    }

    @Test
    @DisplayName("findByIds: delega y retorna lista")
    void findByIds_shouldDelegate() {
        // Arrange
        List<UUID> ids = List.of(ADD_ID);
        List<Add> adds = List.of(sampleAdd());
        when(findingAddPort.findByIds(ids)).thenReturn(adds);
        // Act
        List<Add> result = useCase.findByIds(ids);
        // Assert
        assertThat(result).isEqualTo(adds);
        verify(findingAddPort, times(1)).findByIds(ids);
    }

    private Add sampleAdd() {
        return new Add(
                UUID.randomUUID(),
                "content",
                AddType.TEXT_BANNER,
                null,
                false,
                null,
                true,
                "desc",
                CINEMA_ID,
                USER_ID,
                PaymentState.PENDING,
                null,
                new BigDecimal("10.00"),
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}