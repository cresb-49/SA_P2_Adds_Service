package com.sap.adds_service.adds.infrastructure.output.jpa.specifications;

import com.sap.adds_service.adds.application.usecases.findadd.dtos.AddFilter;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.adds_service.adds.infrastructure.output.jpa.entity.AddEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddEntitySpecsTest {

    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void byFilter_allNull_returnsNullPredicate() {
        // Arrange
        AddFilter filter = new AddFilter(null, null, null, null, null);
        Specification<AddEntity> spec = AddEntitySpecs.byFilter(filter);
        Root<AddEntity> root = mock(Root.class);
        CriteriaQuery<?> cq = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        // Act
        Predicate result = spec.toPredicate(root, cq, cb);
        // Assert
        assertThat(result).isNull();
        verifyNoInteractions(root, cb);
    }

    @Test
    void byFilter_onlyType_buildsEqualOnType() {
        // Arrange
        AddFilter filter = new AddFilter(AddType.TEXT_BANNER, null, null, null, null);
        Specification<AddEntity> spec = AddEntitySpecs.byFilter(filter);
        Root<AddEntity> root = mock(Root.class);
        CriteriaQuery<?> cq = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        Path<Object> typePath = mock(Path.class);
        when(root.get("type")).thenReturn(typePath);
        when(cb.equal(typePath, AddType.TEXT_BANNER)).thenReturn(mock(Predicate.class));
        // Act
        Predicate result = spec.toPredicate(root, cq, cb);
        // Assert
        assertThat(result).isNotNull();
        verify(root).get("type");
        verify(cb).equal(typePath, AddType.TEXT_BANNER);
    }

    @Test
    void byFilter_onlyActive_buildsEqualOnActive() {
        // Arrange
        AddFilter filter = new AddFilter(null, null, true, null, null);
        Specification<AddEntity> spec = AddEntitySpecs.byFilter(filter);
        Root<AddEntity> root = mock(Root.class);
        CriteriaQuery<?> cq = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        Path<Object> activePath = mock(Path.class);
        when(root.get("active")).thenReturn(activePath);
        when(cb.equal(activePath, true)).thenReturn(mock(Predicate.class));
        // Act
        Predicate result = spec.toPredicate(root, cq, cb);
        // Assert
        assertThat(result).isNotNull();
        verify(root).get("active");
        verify(cb).equal(activePath, true);
    }

    @Test
    void byFilter_onlyCinema_buildsEqualOnCinemaId() {
        // Arrange
        AddFilter filter = new AddFilter(null, null, null, CINEMA_ID, null);
        Specification<AddEntity> spec = AddEntitySpecs.byFilter(filter);
        Root<AddEntity> root = mock(Root.class);
        CriteriaQuery<?> cq = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        Path<Object> cinemaPath = mock(Path.class);
        when(root.get("cinemaId")).thenReturn(cinemaPath);
        when(cb.equal(cinemaPath, CINEMA_ID)).thenReturn(mock(Predicate.class));
        // Act
        Predicate result = spec.toPredicate(root, cq, cb);
        // Assert
        assertThat(result).isNotNull();
        verify(root).get("cinemaId");
        verify(cb).equal(cinemaPath, CINEMA_ID);
    }

    @Test
    void byFilter_onlyUser_buildsEqualOnUserId() {
        // Arrange
        AddFilter filter = new AddFilter(null, null, null, null, USER_ID);
        Specification<AddEntity> spec = AddEntitySpecs.byFilter(filter);
        Root<AddEntity> root = mock(Root.class);
        CriteriaQuery<?> cq = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        Path<Object> userPath = mock(Path.class);
        when(root.get("userId")).thenReturn(userPath);
        when(cb.equal(userPath, USER_ID)).thenReturn(mock(Predicate.class));
        // Act
        Predicate result = spec.toPredicate(root, cq, cb);
        // Assert
        assertThat(result).isNotNull();
        verify(root).get("userId");
        verify(cb).equal(userPath, USER_ID);
    }

    @Test
    void byFilter_onlyPaymentState_buildsEqualOnPaymentState() {
        // Arrange
        AddFilter filter = new AddFilter(null, PaymentState.COMPLETED, null, null, null);
        Specification<AddEntity> spec = AddEntitySpecs.byFilter(filter);
        Root<AddEntity> root = mock(Root.class);
        CriteriaQuery<?> cq = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        Path<Object> psPath = mock(Path.class);
        when(root.get("paymentState")).thenReturn(psPath);
        when(cb.equal(psPath, PaymentState.COMPLETED)).thenReturn(mock(Predicate.class));
        // Act
        Predicate result = spec.toPredicate(root, cq, cb);
        // Assert
        assertThat(result).isNotNull();
        verify(root).get("paymentState");
        verify(cb).equal(psPath, PaymentState.COMPLETED);
    }

    @Test
    void byFilter_allFields_buildsEqualsForEach() {
        // Arrange
        AddFilter filter = new AddFilter(AddType.MEDIA_HORIZONTAL, PaymentState.PENDING, false, CINEMA_ID, USER_ID);
        Specification<AddEntity> spec = AddEntitySpecs.byFilter(filter);
        Root<AddEntity> root = mock(Root.class);
        CriteriaQuery<?> cq = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        Path<Object> typePath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Object> activePath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Object> cinemaPath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Object> userPath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Object> psPath = mock(Path.class);

        when(root.get("type")).thenReturn(typePath);
        when(root.get("active")).thenReturn(activePath);
        when(root.get("cinemaId")).thenReturn(cinemaPath);
        when(root.get("userId")).thenReturn(userPath);
        when(root.get("paymentState")).thenReturn(psPath);

        when(cb.equal(typePath, AddType.MEDIA_HORIZONTAL)).thenReturn(mock(Predicate.class));
        when(cb.equal(activePath, false)).thenReturn(mock(Predicate.class));
        when(cb.equal(cinemaPath, CINEMA_ID)).thenReturn(mock(Predicate.class));
        when(cb.equal(userPath, USER_ID)).thenReturn(mock(Predicate.class));
        when(cb.equal(psPath, PaymentState.PENDING)).thenReturn(mock(Predicate.class));

        // Act
        Predicate result = spec.toPredicate(root, cq, cb);

        // Assert
        assertThat(result).isNotNull();
        verify(cb).equal(typePath, AddType.MEDIA_HORIZONTAL);
        verify(cb).equal(activePath, false);
        verify(cb).equal(cinemaPath, CINEMA_ID);
        verify(cb).equal(userPath, USER_ID);
        verify(cb).equal(psPath, PaymentState.PENDING);
    }
}