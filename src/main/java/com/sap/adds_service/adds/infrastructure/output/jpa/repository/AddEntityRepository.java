package com.sap.adds_service.adds.infrastructure.output.jpa.repository;

import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.infrastructure.output.jpa.entity.AddEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddEntityRepository extends JpaRepository<AddEntity, UUID>, JpaSpecificationExecutor<AddEntity> {
    Page<AddEntity> findByType(AddType type, Pageable pageable);

    Page<AddEntity> findByActive(boolean active, Pageable pageable);

    List<AddEntity> findByIdIn(List<UUID> ids);

    Page<AddEntity> findByCinemaId(UUID cinemaId, Pageable pageable);

    Page<AddEntity> findByUserId(UUID userId, Pageable pageable);

    @Query(value = """
              SELECT *
              FROM adds
              WHERE type = :type
                AND cinema_id = :cinemaId
                AND active = true
                AND payment_state = :paymentState
              ORDER BY random()
              LIMIT 1
            """,
            nativeQuery = true)
    Optional<AddEntity> findRandomByTypeCinemaAndPaymentState(
            @Param("type") String type,
            @Param("cinemaId") UUID cinemaId,
            @Param("paymentState") String paymentState
    );

    @Query(value = """
              SELECT *
              FROM adds
              WHERE type = :type
                AND cinema_id = :cinemaId
                AND active = true
                AND payment_state = :paymentState
                AND :currentTime <= add_expiration
              ORDER BY random()
              LIMIT 1
            """,
            nativeQuery = true)
    Optional<AddEntity> findRandomValidByTypeCinemaPaymentStateAndNow(
            @Param("type") String type,
            @Param("cinemaId") UUID cinemaId,
            @Param("paymentState") String paymentState,
            @Param("currentTime") LocalDateTime currentTime
    );

    @Query(
            value = """
                    SELECT *
                    FROM adds a
                    WHERE a.payment_state = :paymentState
                      AND a.paid_at >= :from
                      AND a.paid_at <  :to
                      AND (:type IS NULL OR a.type = :type)
                      AND (
                           CAST(:periodFrom AS date) IS NULL OR CAST(:periodTo AS date) IS NULL
                           OR (a.paid_at::date <= CAST(:periodTo AS date) AND a.add_expiration::date >= CAST(:periodFrom AS date))
                      )
                    ORDER BY a.paid_at DESC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM adds a
                    WHERE a.payment_state = :paymentState
                      AND a.paid_at >= :from
                      AND a.paid_at <  :to
                      AND (:type IS NULL OR a.type = :type)
                      AND (
                           CAST(:periodFrom AS date) IS NULL OR CAST(:periodTo AS date) IS NULL
                           OR (a.paid_at::date <= CAST(:periodTo AS date) AND a.add_expiration::date >= CAST(:periodFrom AS date))
                      )
                    """,
            nativeQuery = true
    )
    List<AddEntity> findPurchasedAdds(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("type") String type, // usa String para mantener consistencia con tus otros @Query nativos
            @Param("periodFrom") java.time.LocalDate periodFrom,
            @Param("periodTo") java.time.LocalDate periodTo,
            @Param("paymentState") String paymentState // típicamente "COMPLETED"
    );
}
