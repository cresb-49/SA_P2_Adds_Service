

package com.sap.adds_service.adds.infrastructure.output.web;

import com.sap.adds_service.adds.domain.dtos.CinemaView;
import com.sap.adds_service.adds.infrastructure.output.web.mapper.CinemaViewMapper;
import com.sap.adds_service.common.infrastructure.output.dtos.CinemaResponseDTO;
import com.sap.adds_service.common.infrastructure.output.dtos.CompanyResponseDTO;
import com.sap.adds_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddCinemaWebAdapterTest {

    @Mock
    private CinemaGatewayPort cinemaGatewayPort;

    @Mock
    private CinemaViewMapper cinemaViewMapper;

    @InjectMocks
    private AddCinemaWebAdapter adapter;

    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID ANOTHER_CINEMA_ID = UUID.randomUUID();
    private static final CompanyResponseDTO COMPANY = new CompanyResponseDTO(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "Company Name",
            "123 Main St",
            "555-1234"
    );
    private static final BigDecimal COST_PER_DAY = BigDecimal.TEN;
    private static final LocalDate CREATED_AT = LocalDate.of(2024, 1, 1);

    @Test
    void checkIfCinemaExistsById_shouldReturnTrue_whenGatewayReturnsTrue() {
        // Arrange
        when(cinemaGatewayPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(true);
        // Act
        boolean result = adapter.checkIfCinemaExistsById(CINEMA_ID);
        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void checkIfCinemaExistsById_shouldReturnFalse_whenGatewayReturnsFalse() {
        // Arrange
        when(cinemaGatewayPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(false);
        // Act
        boolean result = adapter.checkIfCinemaExistsById(CINEMA_ID);
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void findById_shouldReturnMappedCinemaView() {
        // Arrange
        var cinemaResponseDTO = createCinemaResponseDTO(CINEMA_ID, "Cinema One");
        var expectedView = new CinemaView(CINEMA_ID, "Cinema One");
        when(cinemaGatewayPort.findById(CINEMA_ID)).thenReturn(cinemaResponseDTO);
        when(cinemaViewMapper.toDomain(cinemaResponseDTO)).thenReturn(expectedView);
        // Act
        var result = adapter.findById(CINEMA_ID);
        // Assert
        assertThat(result).isEqualTo(expectedView);
    }

    @Test
    void findByIds_shouldReturnMappedCinemaViews() {
        // Arrange
        var ids = List.of(CINEMA_ID, ANOTHER_CINEMA_ID);
        var cinemaResponseDTOs = List.of(
                createCinemaResponseDTO(CINEMA_ID, "Cinema One"),
                createCinemaResponseDTO(ANOTHER_CINEMA_ID, "Cinema Two")
        );
        var expectedViews = List.of(
                new CinemaView(CINEMA_ID, "Cinema One"),
                new CinemaView(ANOTHER_CINEMA_ID, "Cinema Two")
        );
        when(cinemaGatewayPort.findByIds(ids)).thenReturn(cinemaResponseDTOs);
        when(cinemaViewMapper.toDomainList(cinemaResponseDTOs)).thenReturn(expectedViews);
        // Act
        var result = adapter.findByIds(ids);
        // Assert
        assertThat(result).isEqualTo(expectedViews);
    }

    private CinemaResponseDTO createCinemaResponseDTO(UUID id, String name) {
        return new CinemaResponseDTO(id, COMPANY, name, COST_PER_DAY, CREATED_AT);
    }
}
