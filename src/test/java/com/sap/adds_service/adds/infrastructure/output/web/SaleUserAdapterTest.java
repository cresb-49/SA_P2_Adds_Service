package com.sap.adds_service.adds.infrastructure.output.web;

import com.sap.adds_service.adds.domain.dtos.UserView;
import com.sap.adds_service.adds.infrastructure.output.web.mapper.UserViewMapper;
import com.sap.adds_service.common.infrastructure.output.web.port.UserGatewayPort;
import com.sap.common_lib.dto.response.users.profile.ProfileResponseDTO;
import com.sap.common_lib.dto.response.users.user.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleUserAdapterTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ANOTHER_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock
    private UserGatewayPort userGatewayPort;

    @Mock
    private UserViewMapper userViewMapper;

    @InjectMocks
    private SaleUserAdapter adapter;

    @Test
    void existsById_shouldReturnTrue_whenUserExists() {
        // Arrange
        when(userGatewayPort.existsById(USER_ID)).thenReturn(true);
        // Act
        boolean result = adapter.existsById(USER_ID);
        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void existsById_shouldReturnFalse_whenUserDoesNotExist() {
        // Arrange
        when(userGatewayPort.existsById(USER_ID)).thenReturn(false);
        // Act
        boolean result = adapter.existsById(USER_ID);
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void findById_shouldReturnMappedUserView() {
        // Arrange
        var userResponseDTO = createUserResponse(USER_ID, "Jane", "Doe", "jane.doe@example.com");
        var expectedView = new UserView(USER_ID, "Jane", "Doe", "jane.doe@example.com");
        when(userGatewayPort.findById(USER_ID)).thenReturn(userResponseDTO);
        when(userViewMapper.toDomain(userResponseDTO)).thenReturn(expectedView);
        // Act
        var result = adapter.findById(USER_ID);
        // Assert
        assertThat(result).isEqualTo(expectedView);
    }

    @Test
    void findByIds_shouldReturnMappedUserViews() {
        // Arrange
        var ids = List.of(USER_ID, ANOTHER_USER_ID);
        var userResponseDTOs = List.of(
                createUserResponse(USER_ID, "Jane", "Doe", "jane.doe@example.com"),
                createUserResponse(ANOTHER_USER_ID, "John", "Smith", "john.smith@example.com")
        );
        var expectedViews = List.of(
                new UserView(USER_ID, "Jane", "Doe", "jane.doe@example.com"),
                new UserView(ANOTHER_USER_ID, "John", "Smith", "john.smith@example.com")
        );
        when(userGatewayPort.findByIds(ids)).thenReturn(userResponseDTOs);
        when(userViewMapper.toDomainList(userResponseDTOs)).thenReturn(expectedViews);
        // Act
        var result = adapter.findByIds(ids);
        // Assert
        assertThat(result).isEqualTo(expectedViews);
    }

    private UserResponseDTO createUserResponse(UUID id, String firstName, String lastName, String email) {
        var profile = new ProfileResponseDTO(UUID.randomUUID(), firstName, lastName);
        return new UserResponseDTO(id, email, "ROLE_USER", profile);
    }
}
