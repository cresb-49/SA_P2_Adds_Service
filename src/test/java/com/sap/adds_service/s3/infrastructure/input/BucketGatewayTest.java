

package com.sap.adds_service.s3.infrastructure.input;

import com.sap.adds_service.s3.infrastructure.output.adapter.S3ServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BucketGatewayTest {

    @Mock
    private S3ServicePort s3ServicePort;

    @InjectMocks
    private BucketGateway gateway;

    private static final String BUCKET = "bucket";
    private static final String DIR = "dir";
    private static final String KEY = "file.png";

    @Test
    void uploadFileFromBytes_shouldDelegateToService() {
        // Arrange
        byte[] data = new byte[] {1, 2, 3};
        // Act
        gateway.uploadFileFromBytes(BUCKET, DIR, KEY, data);
        // Assert
        verify(s3ServicePort).uploadFileFromBytes(BUCKET, DIR, KEY, data);
    }

    @Test
    void uploadFileFromFile_shouldDelegateToService() {
        // Arrange
        File file = new File("any.txt");
        // Act
        gateway.uploadFileFromFile(BUCKET, DIR, KEY, file);
        // Assert
        verify(s3ServicePort).uploadFileFromFile(BUCKET, DIR, KEY, file);
    }

    @Test
    void downloadFile_shouldReturnBytes_whenServiceSucceeds() throws Exception {
        // Arrange
        byte[] expected = new byte[] {9, 8, 7};
        when(s3ServicePort.downloadFile(BUCKET, DIR, KEY)).thenReturn(expected);
        // Act
        byte[] result = gateway.downloadFile(BUCKET, DIR, KEY);
        // Assert
        assertThat(result).isEqualTo(expected);
        verify(s3ServicePort).downloadFile(BUCKET, DIR, KEY);
    }

    @Test
    void downloadFile_shouldPropagateIOException_whenServiceFails() throws Exception {
        // Arrange
        when(s3ServicePort.downloadFile(BUCKET, DIR, KEY)).thenThrow(new IOException());
        // Act & Assert
        assertThatThrownBy(() -> gateway.downloadFile(BUCKET, DIR, KEY))
                .isInstanceOf(IOException.class);
        verify(s3ServicePort).downloadFile(BUCKET, DIR, KEY);
    }

    @Test
    void deleteFile_shouldDelegateToService() {
        // Arrange
        // Act
        gateway.deleteFile(BUCKET, DIR, KEY);
        // Assert
        verify(s3ServicePort).deleteFile(BUCKET, DIR, KEY);
    }
}