package com.emily.freelance.response;

import lombok.Data;

import java.util.UUID;

@Data
public class TranslationResponse {
    private UUID id;
    private String sourceText;
    private String targetLanguage;
    private Integer wordCount;
    private String deadline;
    private String notes;

}