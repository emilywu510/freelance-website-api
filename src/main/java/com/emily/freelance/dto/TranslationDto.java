package com.emily.freelance.dto;

import lombok.Data;

@Data
public class TranslationDto {
    private String translationId;
    private String userId;
    private String fileName;
    private byte[] fileContent;
}