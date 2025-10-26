package com.sap.adds_service.adds.infrastructure.output.web.mapper;

import com.sap.adds_service.adds.domain.dtos.UserView;
import com.sap.common_lib.dto.response.users.user.UserResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class UserViewMapper {

    public UserView toDomain(UserResponseDTO userResponseDTO) {
        return new UserView(
                userResponseDTO.id(),
                userResponseDTO.profile().firstName(),
                userResponseDTO.profile().lastName(),
                userResponseDTO.email()
        );
    }

    public List<UserView> toDomainList(List<UserResponseDTO> userResponseDTOs) {
        return userResponseDTOs.stream()
                .map(this::toDomain)
                .toList();
    }
}
