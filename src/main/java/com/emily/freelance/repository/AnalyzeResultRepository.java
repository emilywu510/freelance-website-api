package com.emily.freelance.repository;

import com.emily.freelance.entity.AnalyzeResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalyzeResultRepository extends JpaRepository<AnalyzeResult, String> {
    Optional<AnalyzeResult> findByJobId(String jobId);
}