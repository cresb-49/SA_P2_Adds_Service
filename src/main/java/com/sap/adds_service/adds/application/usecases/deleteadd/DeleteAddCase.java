package com.sap.adds_service.adds.application.usecases.deleteadd;

import com.sap.adds_service.adds.application.input.DeleteAddPort;
import com.sap.adds_service.adds.application.output.DeletingAddPort;
import com.sap.adds_service.adds.application.output.DeletingFilePort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.domain.PaymentState;
import com.sap.common_lib.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DeleteAddCase implements DeleteAddPort {

    @Value("${bucket.name}")
    private String bucketName;

    @Value("${bucket.directory}")
    private String bucketDirectory;

    @Value("${aws.region}")
    private String awsRegion;

    private final DeletingFilePort deletingFilePort;
    private final DeletingAddPort deletingAddPort;
    private final FindingAddPort findingAddPort;

    @Override
    public void delete(UUID id) {
        var add = findingAddPort.findById(id).orElseThrow(
                () -> new NotFoundException("Anuncio no encontrado")
        );
        //Verify if te add is not pending or completed payments
        if(add.getPaymentState() == PaymentState.COMPLETED || add.getPaymentState() == PaymentState.PENDING){
            throw new IllegalStateException("No se puede eliminar un anuncio con pagos pendientes o completados");
        }
        deletingAddPort.deleteById(id);
        if (add.getUrlContent() != null && !add.isExternalMedia()) {
            try {
                var urlParts = add.getUrlContent().split("/");
                var keyName = urlParts[urlParts.length - 1];
                System.out.println("Deleting file with key: " + keyName);
                deletingFilePort.deleteFile(bucketName, bucketDirectory, keyName);
            } catch (Exception e) {
                throw new RuntimeException("Error al eliminar el archivo: " + e.getMessage());
            }
        }
    }
}
