package com.sap.adds_service.adds.application.usecases.createadd;

import com.sap.adds_service.adds.application.input.CreateAddPort;
import com.sap.adds_service.adds.application.output.FindDurationPort;
import com.sap.adds_service.adds.application.output.FindingPricePort;
import com.sap.adds_service.adds.application.output.SaveAddPort;
import com.sap.adds_service.adds.application.output.SaveFilePort;
import com.sap.adds_service.adds.application.usecases.createadd.dtos.CreateAddDTO;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.AddType;
import com.sap.adds_service.adds.domain.PriceView;
import com.sap.common_lib.exception.NotFoundException;
import com.sap.common_lib.util.ContentTypeUtils;
import com.sap.common_lib.util.DetectMineTypeResourceUtil;
import com.sap.common_lib.util.FileExtensionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

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
    private final FindingPricePort findingPricePort;
    private final FindDurationPort findDurationPort;

    @Override
    public Add create(CreateAddDTO createAddDTO) {
        //Check if cinema has prices set
        var price = findingPricePort.findByCinemaId(createAddDTO.cinemaId()).orElseThrow(
                () -> new NotFoundException("Cinema must have prices set before creating an add")
        );
        // Check if duration is valid
        var duration = findDurationPort.findById(createAddDTO.durationDaysId()).orElseThrow(
                () -> new NotFoundException("Duration not found")
        );
        //Get a current timestamp
        var now = String.valueOf(System.currentTimeMillis());
        var useExternalUrl = createAddDTO.urlContent() != null && !createAddDTO.urlContent().isBlank();
        var containsFile = createAddDTO.file() != null && !createAddDTO.file().isEmpty();
        if (useExternalUrl && containsFile) {
            throw new IllegalArgumentException("Cannot provide both a file and an external URL");
        }
        var originalFileName = containsFile ? createAddDTO.file().getOriginalFilename() : null;
        var extension = containsFile ? FileExtensionUtils.getExtensionNoDotLower(originalFileName) : "";
        if (containsFile && !extension.matches("^(png|jpg|jpeg|mp4|mov|gif)$")) {
            throw new IllegalArgumentException("File must be png, jpg, jpeg, mp4, mov or gif");
        }
        var isYouTubeUrl = useExternalUrl && isYouTubeUrl(createAddDTO.urlContent());
        var contentTypeExternal = isYouTubeUrl ? "youtube" : useExternalUrl ? DetectMineTypeResourceUtil.detectMimeTypeFromUrl(createAddDTO.urlContent()) : null;
        var localContentType = containsFile ? ContentTypeUtils.detectMimeFromName(originalFileName).orElseThrow(
                () -> new IllegalArgumentException("Could not determine file type")
        ) : null;
        var contentType = isYouTubeUrl ? "youtube" : useExternalUrl ? contentTypeExternal : localContentType;
        //Calculate URL
        var url = useExternalUrl ? createAddDTO.urlContent() : (containsFile ? calculateUrl(extension, now) : null);
        //Create add Domain object
        Add add = new Add(
                createAddDTO.content(),
                createAddDTO.type(),
                contentType,
                useExternalUrl,
                url,
                createAddDTO.description(),
                createAddDTO.cinemaId(),
                createAddDTO.userId(),
                duration.days(),
                this.getPriceByType(createAddDTO.type(), price)
        );
        //Validate Add
        add.validate();
        //Save File
        if (containsFile) {
            saveFile(createAddDTO.file(), extension, now);
        }
        //Save Add
        return saveAddPort.save(add);
    }
    private BigDecimal getPriceByType(AddType addType, PriceView price) {
        return switch (addType) {
            case MEDIA_HORIZONTAL -> price.amountMediaHorizontal();
            case MEDIA_VERTICAL -> price.amountMediaVertical();
            case TEXT_BANNER -> price.amountTextBanner();
        };
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
}
