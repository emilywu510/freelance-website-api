package com.emily.freelance.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "translations")
public class Translation {


    @Id
    @GeneratedValue
    private UUID id;

    private String sourceFileName;
    private String targetLanguage;
    private Integer wordCount;
    private Double amount;
    private LocalDateTime deadline;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    // 關聯 User存UUID (FK)
    private User user;

}