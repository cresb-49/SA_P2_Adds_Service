package com.sap.adds_service.adds.application.output;

import com.sap.adds_service.adds.domain.dtos.UserView;

import java.util.List;
import java.util.UUID;

public interface FindUserPort {
    boolean existsById(UUID userId);

    List<UserView> findByIds(List<UUID> ids);

    UserView findById(UUID id);
}
