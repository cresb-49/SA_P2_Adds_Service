

package com.sap.adds_service.adds.application.usecases.deleteadd;

import com.sap.adds_service.adds.application.output.DeletingAddPort;
import com.sap.adds_service.adds.application.output.DeletingFilePort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.common_lib.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteAddCaseTest {

    private static final UUID ADD_ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String URL = "https://bucket.s3.region.amazonaws.com/folder/file.png";

    private static final String BUCKET = "bucket";
    private static final String DIR = "folder";
    private static final String REGION = "region";

    @Mock
    private DeletingFilePort deletingFilePort;

    @Mock
    private DeletingAddPort deletingAddPort;

    @Mock
    private FindingAddPort findingAddPort;

    @InjectMocks
    private DeleteAddCase deleteAddCase;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        setField(deleteAddCase, "bucketName", BUCKET);
        setField(deleteAddCase, "bucketDirectory", DIR);
        setField(deleteAddCase, "awsRegion", REGION);
    }

    @Test
    void delete_shouldDeleteAddAndFileSuccessfully() {
        // Arrange
        Add add = buildAdd(PaymentState.FAILED, false, URL);
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.of(add));

        // Act
        deleteAddCase.delete(ADD_ID);

        // Assert
        verify(deletingAddPort).deleteById(ADD_ID);
        verify(deletingFilePort).deleteFile("bucket", "folder", "file.png");
    }

    @Test
    void delete_shouldThrowNotFound_whenAddDoesNotExist() {
        // Arrange
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> deleteAddCase.delete(ADD_ID));
        verifyNoInteractions(deletingAddPort, deletingFilePort);
    }

    @Test
    void delete_shouldThrow_whenPaymentIsPending() {
        // Arrange
        Add add = buildAdd(PaymentState.PENDING, false, URL);
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.of(add));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> deleteAddCase.delete(ADD_ID));
        verifyNoInteractions(deletingAddPort, deletingFilePort);
    }

    @Test
    void delete_shouldThrow_whenPaymentIsCompleted() {
        // Arrange
        Add add = buildAdd(PaymentState.COMPLETED, false, URL);
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.of(add));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> deleteAddCase.delete(ADD_ID));
        verifyNoInteractions(deletingAddPort, deletingFilePort);
    }

    @Test
    void delete_shouldNotDeleteFile_whenExternalMediaIsTrue() {
        // Arrange
        Add add = buildAdd(PaymentState.FAILED, true, URL);
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.of(add));

        // Act
        deleteAddCase.delete(ADD_ID);

        // Assert
        verify(deletingAddPort).deleteById(ADD_ID);
        verifyNoInteractions(deletingFilePort);
    }

    @Test
    void delete_shouldNotDeleteFile_whenUrlIsNull() {
        // Arrange
        Add add = buildAdd(PaymentState.FAILED, false, null);
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.of(add));

        // Act
        deleteAddCase.delete(ADD_ID);

        // Assert
        verify(deletingAddPort).deleteById(ADD_ID);
        verifyNoInteractions(deletingFilePort);
    }

    private Add buildAdd(PaymentState paymentState, boolean external, String url) {
        return new Add(
                ADD_ID,
                "content",
                AddType.MEDIA_HORIZONTAL,
                "image/png",
                external,
                url,
                true,
                "desc",
                CINEMA_ID,
                USER_ID,
                paymentState,
                null,
                BigDecimal.TEN,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private static void setField(Object target, String field, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }
}