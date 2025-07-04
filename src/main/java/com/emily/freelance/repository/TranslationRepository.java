package com.emily.freelance.repository;

import com.emily.freelance.entity.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TranslationRepository extends JpaRepository<Translation, UUID> {
    Page<Translation> findByUserId(UUID userId, Pageable pageable);
}