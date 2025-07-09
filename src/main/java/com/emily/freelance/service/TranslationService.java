package com.emily.freelance.service;

import com.emily.freelance.config.RabbitConfig;
import com.emily.freelance.entity.AnalyzeResult;
import com.emily.freelance.entity.Translation;
import com.emily.freelance.entity.User;
import com.emily.freelance.repository.AnalyzeResultRepository;
import com.emily.freelance.repository.TranslationRepository;
import com.emily.freelance.repository.UserRepository;
import com.emily.freelance.util.JwtUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class TranslationService {

    @Autowired
    private TranslationRepository translationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnalyzeResultRepository jobResultRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public String submitAnalysisJob(String authHeader, MultipartFile file) throws IOException {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtUtil.extractUserId(token);
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found."));

        String jobId = UUID.randomUUID().toString();

        // 先存 PENDING狀態到 DB
        AnalyzeResult result = new AnalyzeResult();
        result.setJobId(jobId);
        result.setUserId(userId);
        result.setStatus("PENDING");
        jobResultRepository.save(result);

        // 丟到 RabbitMQ
        Map<String, Object> message = new HashMap<>();
        message.put("fileBytes", file.getBytes());
        message.put("userId", userId.toString());
        message.put("jobId", jobId);

        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, message);

        return jobId;
    }

    public Optional<AnalyzeResult> getAnalysisResult(String jobId) {
        return jobResultRepository.findByJobId(jobId);
    }

    public String createTranslation(String authHeader, MultipartFile file,
                                    String targetLanguage, Integer wordCount, Double amount,
                                    String deadline, String notes) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty!");
        }

        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtUtil.extractUserId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // 使用userId建立子資料夾路徑
        Path userFolder = Paths.get("uploads/" + userId.toString());
        // 沒資料夾就建立
        Files.createDirectories(userFolder);
        // 在該資料夾中建立檔案路徑，檔案名稱 + userId、系統時間避免重複
        String filename = userId + "_" + file.getOriginalFilename()+ "_" + System.currentTimeMillis();
        Path filePath = userFolder.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Translation translation = new Translation();
        translation.setSourceFileName(file.getOriginalFilename());
        translation.setTargetLanguage(targetLanguage);
        translation.setWordCount(wordCount);
        translation.setAmount(amount);
        translation.setDeadline(LocalDateTime.parse(deadline));
        translation.setNotes(notes);
        translation.setUser(user);
        translationRepository.save(translation);

        return "Translation submitted!";
    }

    public Page<Translation> getTranslations(String authHeader, int page, int size) {
        // 分頁查詢翻譯記錄
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtUtil.extractUserId(token);

        return translationRepository.findByUserId(userId, PageRequest.of(page, size));
    }
}
