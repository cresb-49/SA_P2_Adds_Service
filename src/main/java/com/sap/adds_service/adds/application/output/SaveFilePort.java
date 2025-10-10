package com.sap.adds_service.adds.application.output;

public interface SaveFilePort {
    void uploadFile(String bucketName, String directory, String keyName, byte[] fileData);
}
