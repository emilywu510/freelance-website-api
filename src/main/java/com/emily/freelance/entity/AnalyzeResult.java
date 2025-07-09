package com.emily.freelance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "analyze_results")
public class AnalyzeResult {

    @Id
    private String jobId;

    private UUID userId;

    private Integer wordCount;

    private Double amount;

    private String status;

}
