package com.sap.adds_service.adds.application.usecases.updateadd;

import com.sap.adds_service.adds.application.output.DeletingFilePort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.application.output.SaveFilePort;
import com.sap.adds_service.adds.application.usecases.updateadd.dtos.UpdateAddDTO;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.common_lib.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAddCaseTest {

    @Mock private FindingAddPort findingAddPort;
    @Mock private SaveFilePort saveFilePort;
    @Mock private SaveAddPort saveAddPort;
    @Mock private DeletingFilePort deletingFilePort;

    @InjectMocks private UpdateAddCase useCase;

    private static final UUID ADD_ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    private static final String BUCKET = "bucket";
    private static final String DIR = "folder";
    private static final String REGION = "region";

    @BeforeEach
    void init() throws Exception {
        setField(useCase, "bucketName", BUCKET);
        setField(useCase, "bucketDirectory", DIR);
        setField(useCase, "awsRegion", REGION);
    }

    @Test
    void update_shouldThrow_whenBothUrlAndFileProvided() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        UpdateAddDTO dto = mock(UpdateAddDTO.class);
        when(dto.urlContent()).thenReturn("https://youtu.be/abc");
        when(dto.file()).thenReturn(file);
        // Act & Assert
        assertThatThrownBy(() -> useCase.update(dto)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(findingAddPort, saveFilePort, deletingFilePort, saveAddPort);
    }

    @Test
    void update_shouldThrow_whenInvalidExtension() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("virus.exe");
        UpdateAddDTO dto = mock(UpdateAddDTO.class);
        when(dto.file()).thenReturn(file);
        when(dto.urlContent()).thenReturn(null);
        // Act & Assert
        assertThatThrownBy(() -> useCase.update(dto)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(findingAddPort, saveFilePort, deletingFilePort, saveAddPort);
    }

    @Test
    void update_shouldThrow_whenAddNotFound() {
        // Arrange
        UpdateAddDTO dto = mock(UpdateAddDTO.class);
        when(dto.id()).thenReturn(ADD_ID);
        when(dto.urlContent()).thenReturn(null);
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        when(dto.file()).thenReturn(file);
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> useCase.update(dto)).isInstanceOf(NotFoundException.class);
        verify(findingAddPort).findById(ADD_ID);
        verifyNoInteractions(saveFilePort, deletingFilePort);
    }

    @Test
    void update_shouldSwitchToYouTubeUrl_andDeleteOldInternal() {
        // Arrange
        Add existing = existingMediaInternal("https://bucket.s3.region.amazonaws.com/folder/old.png");
        UpdateAddDTO dto = mock(UpdateAddDTO.class);
        when(dto.id()).thenReturn(existing.getId());
        when(dto.urlContent()).thenReturn("https://youtu.be/abc123");
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        when(dto.file()).thenReturn(file);
        when(findingAddPort.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(saveAddPort.save(any(Add.class))).thenAnswer(inv -> inv.getArgument(0));
        // Act
        Add result = useCase.update(dto);
        // Assert
        assertThat(result.getUrlContent()).contains("youtu");
        verify(deletingFilePort).deleteFile(any(), any(), eq("old.png"));
        verifyNoInteractions(saveFilePort);
        verify(saveAddPort).save(existing);
    }

    @Test
    void update_shouldUploadLocalPng_andDeleteOldInternal() throws Exception {
        // Arrange
        Add existing = existingMediaInternal("https://bucket.s3.region.amazonaws.com/folder/old.png");
        UpdateAddDTO dto = mock(UpdateAddDTO.class);
        when(dto.id()).thenReturn(existing.getId());
        when(dto.urlContent()).thenReturn(null);
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("banner.png");
        when(file.getBytes()).thenReturn(new byte[]{1,2,3});
        when(dto.file()).thenReturn(file);
        when(findingAddPort.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(saveAddPort.save(any(Add.class))).thenAnswer(inv -> inv.getArgument(0));
        // Act
        Add result = useCase.update(dto);
        // Assert
        assertThat(result.getUrlContent()).isNotNull();
        verify(saveFilePort).uploadFile(any(), any(), anyString(), any());
        verify(deletingFilePort).deleteFile(any(), any(), eq("old.png"));
        verify(saveAddPort).save(existing);
    }

    @Test
    void update_shouldOnlyUpdateFields_withoutFileOrUrl() {
        // Arrange
        Add existing = existingTextBanner();
        UpdateAddDTO dto = mock(UpdateAddDTO.class);
        when(dto.id()).thenReturn(existing.getId());
        when(dto.urlContent()).thenReturn(null);
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        when(dto.file()).thenReturn(file);
        when(dto.content()).thenReturn("Nuevo texto");
        when(dto.description()).thenReturn("Nueva desc");
        when(findingAddPort.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(saveAddPort.save(any(Add.class))).thenAnswer(inv -> inv.getArgument(0));
        // Act
        Add result = useCase.update(dto);
        // Assert
        assertThat(result.getDescription()).isEqualTo("Nueva desc");
        assertThat(result.getContent()).isEqualTo("Nuevo texto");
        verifyNoInteractions(saveFilePort, deletingFilePort);
        verify(saveAddPort).save(existing);
    }

    private Add existingMediaInternal(String url) {
        return new Add(
                UUID.randomUUID(),
                null,
                AddType.MEDIA_HORIZONTAL,
                "image/png",
                false,
                url,
                true,
                "desc",
                CINEMA_ID,
                USER_ID,
                PaymentState.PENDING,
                null,
                BigDecimal.TEN,
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Add existingTextBanner() {
        return new Add(
                UUID.randomUUID(),
                "Texto",
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
                BigDecimal.ONE,
                LocalDateTime.now().plusDays(3),
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