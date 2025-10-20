

package com.sap.adds_service.adds.application.usecases.changestatecase;

import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.common_lib.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeStateAddCaseTest {

    @Mock
    private FindingAddPort findingAddPort;

    @Mock
    private SaveAddPort saveAddPort;

    @InjectMocks
    private ChangeStateAddCase useCase;

    private static final UUID ADD_ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String DESC = "Publicidad principal";
    private static final String CONTENT = "Compra ya";
    private static final BigDecimal PRICE = new BigDecimal("9.99");

    @Test
    @DisplayName("debe alternar el estado y guardar cuando el anuncio existe")
    void changeState_shouldToggleAndSave_whenFound() {
        // Arrange
        Add add = new Add(CONTENT, AddType.TEXT_BANNER, null, false, null, DESC, CINEMA_ID, USER_ID, 7, PRICE);
        add.markAsPaid(); // requerido para permitir changeActive()
        assertThat(add.getPaymentState()).isEqualTo(PaymentState.COMPLETED);
        boolean before = add.isActive();
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.of(add));
        when(saveAddPort.save(any(Add.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Add result = useCase.changeState(ADD_ID);

        // Assert
        verify(findingAddPort, times(1)).findById(ADD_ID);
        verify(saveAddPort, times(1)).save(add);
        assertThat(result.isActive()).isEqualTo(!before);
    }

    @Test
    @DisplayName("debe lanzar NotFoundException cuando no existe el anuncio")
    void changeState_shouldThrow_whenNotFound() {
        // Arrange
        when(findingAddPort.findById(ADD_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.changeState(ADD_ID)).isInstanceOf(NotFoundException.class);
        verify(findingAddPort, times(1)).findById(ADD_ID);
        verifyNoInteractions(saveAddPort);
    }
}