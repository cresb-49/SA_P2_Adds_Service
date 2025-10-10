package com.sap.adds_service.adds.application.output;

public interface DeletingFilePort {
    void deleteFile(String bucketName, String directory, String keyName);
}
