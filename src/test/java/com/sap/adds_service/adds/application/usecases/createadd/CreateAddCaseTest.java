package com.sap.adds_service.adds.application.usecases.createadd;

import com.sap.adds_service.adds.application.output.*;
import com.sap.adds_service.adds.application.usecases.createadd.dtos.CreateAddDTO;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PriceView;
import com.sap.adds_service.adds.domain.DurationView;
import com.sap.common_lib.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAddCaseTest {

    @Mock
    private SaveAddPort saveAddPort;
    @Mock
    private SaveFilePort saveFilePort;
    @Mock
    private FindingPricePort findingPricePort;
    @Mock
    private FindDurationPort findDurationPort;
    @Mock
    private SendPaymentAddPort sendPaymentAddPort;
    @Mock
    private SendNotificationPort sendNotificationPort;
    @Mock
    private FindCinemaPort findCinemaPort;

    @InjectMocks
    private CreateAddCase useCase;

    private static final UUID ADD_ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID DURATION_ID = UUID.randomUUID();
    private static final BigDecimal TXT_PRICE = new BigDecimal("10.00");
    private static final BigDecimal VERT_PRICE = new BigDecimal("20.00");
    private static final BigDecimal HORZ_PRICE = new BigDecimal("30.00");

    private CreateAddDTO mockDto(
            String content,
            AddType type,
            String description,
            String url,
            MultipartFile file) {
        CreateAddDTO dto = mock(CreateAddDTO.class);
        when(dto.content()).thenReturn(content);
        when(dto.type()).thenReturn(type);
        when(dto.description()).thenReturn(description);
        when(dto.cinemaId()).thenReturn(CINEMA_ID);
        when(dto.userId()).thenReturn(USER_ID);
        when(dto.durationDaysId()).thenReturn(DURATION_ID);
        when(dto.urlContent()).thenReturn(url);
        when(dto.file()).thenReturn(file);
        return dto;
    }

    private void stubHappyInfra() {
        when(findCinemaPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(true);
        when(findingPricePort.findByCinemaId(CINEMA_ID))
                .thenReturn(Optional.of(new PriceView(ADD_ID, CINEMA_ID, TXT_PRICE, VERT_PRICE, HORZ_PRICE,
                        LocalDateTime.now(), LocalDateTime.now())));
        DurationView durationView = mock(DurationView.class);
        when(findDurationPort.findById(DURATION_ID)).thenReturn(Optional.of(durationView));
    }

    @Test
    @DisplayName("debe lanzar NotFoundException cuando el cine no existe")
    void create_shouldThrow_whenCinemaNotExists() {
        // Arrange
        CreateAddDTO dto = mock(CreateAddDTO.class);
        when(dto.cinemaId()).thenReturn(CINEMA_ID);
        when(findCinemaPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(false);
        // Act & Assert
        assertThatThrownBy(() -> useCase.create(dto)).isInstanceOf(NotFoundException.class);
        verifyNoInteractions(findingPricePort, findDurationPort, saveAddPort, saveFilePort, sendPaymentAddPort,
                sendNotificationPort);
    }

    @Test
    @DisplayName("debe lanzar NotFoundException cuando el cine no tiene precios configurados")
    void create_shouldThrow_whenPricesNotConfigured() {
        // Arrange
        CreateAddDTO dto = mock(CreateAddDTO.class);
        when(dto.cinemaId()).thenReturn(CINEMA_ID);
        when(findCinemaPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(true);
        when(findingPricePort.findByCinemaId(CINEMA_ID)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> useCase.create(dto)).isInstanceOf(NotFoundException.class);
        verify(findCinemaPort).checkIfCinemaExistsById(CINEMA_ID);
        verifyNoMoreInteractions(findCinemaPort);
        verifyNoInteractions(findDurationPort, saveAddPort, saveFilePort, sendPaymentAddPort, sendNotificationPort);
    }

    @Test
    @DisplayName("debe lanzar NotFoundException cuando la duración no existe")
    void create_shouldThrow_whenDurationNotFound() {
        // Arrange
        CreateAddDTO dto = mock(CreateAddDTO.class);
        when(dto.cinemaId()).thenReturn(CINEMA_ID);
        when(dto.durationDaysId()).thenReturn(DURATION_ID);
        when(findCinemaPort.checkIfCinemaExistsById(CINEMA_ID)).thenReturn(true);
        when(findingPricePort.findByCinemaId(CINEMA_ID)).thenReturn(Optional
                .of(new PriceView(ADD_ID, CINEMA_ID, TXT_PRICE, VERT_PRICE, HORZ_PRICE, LocalDateTime.now(), LocalDateTime.now())));
        when(findDurationPort.findById(DURATION_ID)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> useCase.create(dto)).isInstanceOf(NotFoundException.class);
        verifyNoInteractions(saveAddPort, saveFilePort, sendPaymentAddPort, sendNotificationPort);
    }

    @Test
    @DisplayName("debe lanzar IllegalArgumentException cuando URL externa y archivo vienen juntos")
    void create_shouldThrow_whenBothUrlAndFileProvided() throws Exception {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        CreateAddDTO dto = mock(CreateAddDTO.class);
        when(dto.cinemaId()).thenReturn(CINEMA_ID);
        when(dto.durationDaysId()).thenReturn(DURATION_ID);
        when(dto.urlContent()).thenReturn("https://youtu.be/abc");
        when(dto.file()).thenReturn(file);
        stubHappyInfra();
        // Act & Assert
        assertThatThrownBy(() -> useCase.create(dto)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(saveFilePort, saveAddPort);
    }

    @Test
    @DisplayName("debe lanzar IllegalArgumentException cuando la extensión de archivo es inválida")
    void create_shouldThrow_whenInvalidExtension() throws Exception {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("malicioso.exe");
        CreateAddDTO dto = mock(CreateAddDTO.class);
        when(dto.cinemaId()).thenReturn(CINEMA_ID);
        when(dto.durationDaysId()).thenReturn(DURATION_ID);
        when(dto.file()).thenReturn(file);
        when(dto.urlContent()).thenReturn(null);
        stubHappyInfra();
        // Act & Assert
        assertThatThrownBy(() -> useCase.create(dto)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(saveFilePort, saveAddPort);
    }

    @Test
    @DisplayName("crea anuncio con URL de YouTube sin subir archivo")
    void create_shouldSucceed_withYouTubeUrl() {
        // Arrange
        CreateAddDTO dto = mockDto(null, AddType.MEDIA_HORIZONTAL, "desc", "https://youtu.be/abc123", null);
        stubHappyInfra();
        when(saveAddPort.save(any(Add.class))).thenAnswer(inv -> inv.getArgument(0));
        // Act
        Add result = useCase.create(dto);
        // Assert
        assertThat(result).isNotNull();
        verify(sendPaymentAddPort, times(1)).sendPaymentEvent(any());
        verify(sendNotificationPort, times(1)).sendNotification(any());
        verifyNoInteractions(saveFilePort);
        verify(saveAddPort, times(1)).save(any(Add.class));
    }

    @Test
    @DisplayName("crea anuncio de texto sin archivo ni URL externa")
    void create_shouldSucceed_textBanner_withoutFileOrUrl() {
        // Arrange
        CreateAddDTO dto = mockDto("Promo hoy", AddType.TEXT_BANNER, "desc", null, null);
        stubHappyInfra();
        when(saveAddPort.save(any(Add.class))).thenAnswer(inv -> inv.getArgument(0));
        // Act
        Add result = useCase.create(dto);
        // Assert
        assertThat(result).isNotNull();
        verify(sendPaymentAddPort, times(1)).sendPaymentEvent(any());
        verify(sendNotificationPort, times(1)).sendNotification(any());
        verify(saveAddPort, times(1)).save(any(Add.class));
        verifyNoInteractions(saveFilePort);
    }

    @Test
    @DisplayName("crea anuncio con archivo local válido (png)")
    void create_shouldSucceed_withLocalPngFile() throws Exception {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("image.png");
        when(file.getBytes()).thenReturn(new byte[] { 1, 2, 3 });
        CreateAddDTO dto = mockDto(null, AddType.MEDIA_VERTICAL, "desc", null, file);
        stubHappyInfra();
        when(saveAddPort.save(any(Add.class))).thenAnswer(inv -> inv.getArgument(0));
        // Act
        Add result = useCase.create(dto);
        // Assert
        assertThat(result).isNotNull();
        verify(saveFilePort, times(1)).uploadFile(any(), any(), anyString(), any());
        verify(saveAddPort, times(1)).save(any(Add.class));
        verify(sendPaymentAddPort, times(1)).sendPaymentEvent(any());
        verify(sendNotificationPort, times(1)).sendNotification(any());
    }
}