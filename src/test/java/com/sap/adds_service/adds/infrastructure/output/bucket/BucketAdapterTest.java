

package com.sap.adds_service.adds.infrastructure.output.bucket;

import com.sap.adds_service.s3.infrastructure.input.port.BucketGatewayPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BucketAdapterTest {

    @Mock
    private BucketGatewayPort bucketGatewayPort;

    @InjectMocks
    private BucketAdapter adapter;

    private static final String BUCKET = "test-bucket";
    private static final String DIRECTORY = "test-dir";
    private static final String KEY = "file.txt";
    private static final byte[] DATA = new byte[]{1, 2, 3};

    @BeforeEach
    void setup() {}

    @Test
    void uploadFile_shouldDelegateToGateway() {
        // Act
        adapter.uploadFile(BUCKET, DIRECTORY, KEY, DATA);

        // Assert
        verify(bucketGatewayPort).uploadFileFromBytes(BUCKET, DIRECTORY, KEY, DATA);
    }

    @Test
    void deleteFile_shouldDelegateToGateway() {
        // Act
        adapter.deleteFile(BUCKET, DIRECTORY, KEY);

        // Assert
        verify(bucketGatewayPort).deleteFile(BUCKET, DIRECTORY, KEY);
    }
}