package com.sap.adds_service.adds.domain;

import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.UUID;

public class Add {
    private UUID id;
    private String content;
    private AddType type;
    private String urlContent;
}
