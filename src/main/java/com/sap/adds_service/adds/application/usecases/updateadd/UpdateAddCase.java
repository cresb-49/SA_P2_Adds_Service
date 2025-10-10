package com.sap.adds_service.adds.application.usecases.updateadd;

import com.sap.adds_service.adds.application.input.UpdateAddPort;
import com.sap.adds_service.adds.application.output.DeletingFilePort;
import com.sap.adds_service.adds.application.output.FindingAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.application.output.SaveFilePort;
import com.sap.adds_service.adds.application.usecases.updateadd.dtos.UpdateAddDTO;
import com.sap.adds_service.adds.domain.Add;
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
        var containsFile = updateAddDTO.getFile() != null && !updateAddDTO.getFile().isEmpty();
        var originalFileName = containsFile ? updateAddDTO.getFile().getOriginalFilename() : null;
        var extension = containsFile ? getExtensionNoDotLower(originalFileName) : "";
        if (containsFile && !extension.matches("^(png|jpg|jpeg|mp4|mov|gif)$")) {
            throw new RuntimeException("File must be png, jpg, jpeg, mp4, mov or gif");
        }
        //Find existing add
        Add add = findingAddPort.findById(updateAddDTO.getId()).orElseThrow(
                () -> new RuntimeException("Add not found")
        );
        //Calculate new url if there is a new file
        String url = null;
        if (containsFile) {
            url = this.calculateUrl(add, extension, now);
        }
        //Update fields
        add.update(
                updateAddDTO.getContent(),
                updateAddDTO.getActive(),
                updateAddDTO.getDescription(),
                url
        );
        // Validate Add
        add.validate();
        //Delete old file if there is a new file and save new file
        if (containsFile) {
            this.saveFile(add, updateAddDTO.getFile(), extension, now);
            this.deleteFile(add);
        }
        //Save Add
        return saveAddPort.save(add);
    }

    private String getExtensionWithDot(String name) {
        if (name == null) return "";
        String trimmed = name.trim();
        int lastDot = trimmed.lastIndexOf('.');
        // No punto o el punto es el primer char (dotfile) -> sin extensión "convencional"
        if (lastDot <= 0 || lastDot == trimmed.length() - 1) return "";
        return trimmed.substring(lastDot); // incluye el punto
    }

    private String getExtensionNoDotLower(String name) {
        String withDot = getExtensionWithDot(name);
        return withDot.isEmpty() ? "" : withDot.substring(1).toLowerCase(); // sin punto y en minúsculas
    }

    private String calculateUrl(Add add, String extension, String now) {
        var fileName = String.format("add_%s_%s.%s", add.getId(), now, extension);
        return String.format("https://%s.s3.%s.amazonaws.com/%s/%s", bucketName, awsRegion, bucketDirectory, fileName);
    }

    private void saveFile(Add add, MultipartFile file, String extension, String now) {
        try {
            saveFilePort.uploadFile(
                    bucketName,
                    bucketDirectory,
                    String.format("add_%s_%s.%s", add.getId(), now, extension),
                    file.getBytes()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error saving file", e);
        }
    }

    private void deleteFile(Add add) {
        try {
            var urlParts = add.getUrlContent().split("/");
            var keyName = urlParts[urlParts.length - 1];
            deletingFilePort.deleteFile(bucketName, bucketDirectory, keyName);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from bucket: " + e.getMessage());
        }
    }
}
