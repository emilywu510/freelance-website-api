package com.emily.freelance.controller;

import com.emily.freelance.entity.Translation;
import com.emily.freelance.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/translations")
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createTranslation(
            @RequestParam("file") MultipartFile file,
            @RequestParam String targetLanguage,
            @RequestParam Integer wordCount,
            @RequestParam Double amount,
            @RequestParam String deadline,
            @RequestParam(required = false) String notes,
            @RequestHeader("Authorization") String authHeader) throws IOException {

        String result = translationService.createTranslation(
                authHeader, file, targetLanguage, wordCount, amount, deadline, notes);
        return ResponseEntity.ok(Map.of("message", result));
    }

    @GetMapping
    public ResponseEntity<Page<Translation>> getTranslations(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<Translation> translations = translationService.getTranslations(authHeader, page, size);
        return ResponseEntity.ok(translations);
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {
        Map<String, Object> result = translationService.countWords(authHeader,file);
        return ResponseEntity.ok(result);
    }
}
