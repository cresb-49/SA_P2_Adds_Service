package com.sap.adds_service.adds.application.usecases.deleteadd;

import com.sap.adds_service.adds.application.input.DeleteAddPort;
import com.sap.adds_service.adds.application.output.DeletingAddPort;
import com.sap.adds_service.adds.application.output.DeletingFilePort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
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
                () -> new RuntimeException("Add not found")
        );
        if (add.getUrlContent() != null && !add.getUrlContent().isBlank()) {
            try {
                var urlParts = add.getUrlContent().split("/");
                var keyName = urlParts[urlParts.length - 1];
                System.out.println("Deleting file with key: " + keyName);
                deletingFilePort.deleteFile(bucketName, bucketDirectory, keyName);
            } catch (Exception e) {
                throw new RuntimeException("Error deleting file from bucket: " + e.getMessage());
            }
        }
        deletingAddPort.deleteById(id);
    }
}
