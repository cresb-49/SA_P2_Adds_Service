package com.sap.adds_service.adds.infrastructure.output.jpa.specifications;

import com.sap.adds_service.adds.application.usecases.findadd.dtos.AddFilter;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.infrastructure.output.jpa.entity.AddEntity;
import org.springframework.data.jpa.domain.Specification;
import java.util.UUID;

public class AddEntitySpecs {
    public static Specification<AddEntity> byFilter(AddFilter f) {
        return Specification.allOf(
                eqType(f.type()),
                eqActive(f.active()),
                eqCinema(f.cinemaId()),
                eqUser(f.userId())
        );
    }

    private static Specification<AddEntity> eqType(AddType type) {
        return (root, q, cb) -> type == null ? null : cb.equal(root.get("type"), type);
    }

    private static Specification<AddEntity> eqActive(Boolean active) {
        return (root, q, cb) -> active == null ? null : cb.equal(root.get("active"), active);
    }

    private static Specification<AddEntity> eqCinema(UUID cinemaId) {
        return (root, q, cb) -> cinemaId == null ? null : cb.equal(root.get("cinemaId"), cinemaId);
    }

    private static Specification<AddEntity> eqUser(UUID userId) {
        return (root, q, cb) -> userId == null ? null : cb.equal(root.get("userId"), userId);
    }
}
