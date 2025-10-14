package com.sap.adds_service.adds.application.usecases.updateadd;

import com.sap.adds_service.adds.application.input.UpdateAddPort;
import com.sap.adds_service.adds.application.output.DeletingFilePort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.application.output.SaveFilePort;
import com.sap.adds_service.adds.application.usecases.updateadd.dtos.UpdateAddDTO;
import com.sap.adds_service.adds.domain.Add;
import com.sap.common_lib.exception.NotFoundException;
import com.sap.common_lib.util.ContentTypeUtils;
import com.sap.common_lib.util.DetectMineTypeResourceUtil;
import com.sap.common_lib.util.FileExtensionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UpdateAddCase implements UpdateAddPort {

    @Value("${bucket.name}")
    private String bucketName;

    @Value("${bucket.directory}")
    private String bucketDirectory;

    @Value("${aws.region}")
    private String awsRegion;

    private final FindingAddPort findingAddPort;
    private final SaveFilePort saveFilePort;
    private final SaveAddPort saveAddPort;
    private final DeletingFilePort deletingFilePort;

    @Override
    public Add update(UpdateAddDTO updateAddDTO) {
        //Get a current timestamp
        var now = String.valueOf(System.currentTimeMillis());
        var useExternalUrl = updateAddDTO.urlContent() != null && !updateAddDTO.urlContent().isBlank();
        var containsFile = updateAddDTO.file() != null && !updateAddDTO.file().isEmpty();
        if (useExternalUrl && containsFile) {
            throw new IllegalArgumentException("Cannot provide both a file and an external URL");
        }
        var originalFileName = containsFile ? updateAddDTO.file().getOriginalFilename() : null;
        var extension = containsFile ? FileExtensionUtils.getExtensionNoDotLower(originalFileName) : "";
        if (containsFile && !extension.matches("^(png|jpg|jpeg|mp4|mov|gif)$")) {
            throw new IllegalArgumentException("File must be png, jpg, jpeg, mp4, mov or gif");
        }
        //Find existing add
        Add add = findingAddPort.findById(updateAddDTO.id()).orElseThrow(
                () -> new NotFoundException("Add not found")
        );
        var oldIsExternal = add.isExternalMedia();
        var oldUrl = add.getUrlContent();
        // Determine content type
        var isYouTubeUrl = useExternalUrl && isYouTubeUrl(updateAddDTO.urlContent());
        var contentTypeExternal = isYouTubeUrl ? "youtube" : useExternalUrl ? DetectMineTypeResourceUtil.detectMimeTypeFromUrl(updateAddDTO.urlContent()) : null;
        var localContentType = containsFile ? ContentTypeUtils.detectMimeFromName(originalFileName).orElseThrow(
                () -> new IllegalArgumentException("Could not determine file type")
        ) : null;
        var contentType = isYouTubeUrl ? "youtube" : useExternalUrl ? contentTypeExternal : localContentType;
        //Calculate new url if there is a new file
        var url = useExternalUrl ? updateAddDTO.urlContent() : (containsFile ? calculateUrl(extension, now) : null);
        //Update fields
        add.update(
                updateAddDTO.content(),
                updateAddDTO.active(),
                updateAddDTO.description(),
                contentType,
                useExternalUrl,
                url
        );
        // Validate Add
        add.validate();
        //Delete old file if there is a new file and save new file
        if (containsFile) {
            this.saveFile(updateAddDTO.file(), extension, now);
        }
        if (containsFile || useExternalUrl) {
            if (oldUrl != null && !oldIsExternal) {
                this.deleteFile(oldUrl);
            }
        }
        //Save Add
        return saveAddPort.save(add);
    }

    public boolean isYouTubeUrl(String url) {
        return url != null && (
                url.contains("youtube.com/watch") ||
                        url.contains("youtu.be/")
        );
    }

    private String calculateUrl(String extension, String now) {
        var fileName = String.format("add_%s.%s", now, extension);
        return String.format("https://%s.s3.%s.amazonaws.com/%s/%s", bucketName, awsRegion, bucketDirectory, fileName);
    }

    private void saveFile(MultipartFile file, String extension, String now) {
        try {
            saveFilePort.uploadFile(
                    bucketName,
                    bucketDirectory,
                    String.format("add_%s.%s", now, extension),
                    file.getBytes()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error saving file", e);
        }
    }

    private void deleteFile(String url) {
        try {
            var urlParts = url.split("/");
            var keyName = urlParts[urlParts.length - 1];
            deletingFilePort.deleteFile(bucketName, bucketDirectory, keyName);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from bucket: " + e.getMessage());
        }
    }
}
