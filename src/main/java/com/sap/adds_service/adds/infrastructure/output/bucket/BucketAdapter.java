package com.sap.adds_service.adds.infrastructure.output.bucket;

import com.sap.adds_service.adds.application.output.DeletingFilePort;
import com.sap.adds_service.adds.application.output.SaveFilePort;
import com.sap.adds_service.s3.infrastructure.input.port.BucketGatewayPort;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BucketAdapter implements SaveFilePort, DeletingFilePort {

    private final BucketGatewayPort bucketGatewayPort;

    @Override
    public void uploadFile(String bucketName, String directory, String keyName, byte[] fileData) {
        bucketGatewayPort.uploadFileFromBytes(bucketName, directory, keyName, fileData);
    }

    @Override
    public void deleteFile(String bucketName, String directory, String keyName) {
        bucketGatewayPort.deleteFile(bucketName, directory, keyName);
    }
}
