package com.sap.adds_service.adds.application.usecases.createadd;

import com.sap.adds_service.adds.application.input.CreateAddPort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.application.output.SaveFilePort;
import com.sap.adds_service.adds.application.usecases.createadd.dtos.CreateAddDTO;
import com.sap.adds_service.adds.domain.Add;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CreateAddCase implements CreateAddPort {

    @Value("${bucket.name}")
    private String bucketName;

    @Value("${bucket.directory}")
    private String bucketDirectory;

    @Value("${aws.region}")
    private String awsRegion;

    private final SaveAddPort saveAddPort;
    private final SaveFilePort saveFilePort;

    @Override
    public Add create(CreateAddDTO createAddDTO) {
        //Get a current timestamp
        var now = String.valueOf(System.currentTimeMillis());
        var containsFile = createAddDTO.getFile() != null && !createAddDTO.getFile().isEmpty();
        var originalFileName = containsFile ? createAddDTO.getFile().getOriginalFilename() : null;
        var extension = containsFile ? getExtensionNoDotLower(originalFileName) : "";
        if(containsFile && !extension.matches("^(png|jpg|jpeg|mp4|mov|gif)$")) {
            throw new IllegalArgumentException("File must be png, jpg, jpeg, mp4, mov or gif");
        }
        //Create add Domain object
        Add add = Add.builder()
                .content(createAddDTO.getContent())
                .type(createAddDTO.getType())
                .description(createAddDTO.getDescription())
                .cinemaId(createAddDTO.getCinemaId())
                .build();

        //Assign url if there is a file
        if(containsFile) {
            var url = calculateUrl(add, extension, now);
            add = add.toBuilder().urlContent(url).build();
        }
        //Validate Add
        add.validate();
        //Save File
        if(containsFile) {
            saveFile(add, createAddDTO.getFile(), extension, now);
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

    private void saveFile(Add add, MultipartFile file, String extension, String now){
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
}
