package com.emily.freelance.request;

import lombok.Data;

import java.time.LocalDateTime;

//目前用不到，皆用參數接收
@Data
public class TranslationRequest {
    private String sourceText;
    private String targetLanguage;
    private Integer wordCount;
    private LocalDateTime deadline;
    private String notes;

}