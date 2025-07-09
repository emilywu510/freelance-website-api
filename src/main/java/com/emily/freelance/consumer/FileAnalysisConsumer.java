package com.emily.freelance.consumer;

import com.emily.freelance.config.RabbitConfig;
import com.emily.freelance.entity.AnalyzeResult;
import com.emily.freelance.repository.AnalyzeResultRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FileAnalysisConsumer {

    @Autowired
    private AnalyzeResultRepository jobResultRepository;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleFileAnalysis(Map<String, Object> message) throws IOException {
        byte[] fileBytes = (byte[]) message.get("fileBytes");
        UUID userId = UUID.fromString((String) message.get("userId"));
        String jobId = (String) message.get("jobId");

        String content = new String(fileBytes);

        Pattern pattern = Pattern.compile("[a-zA-Z]+(?:['-][a-zA-Z]+)*");
        Matcher matcher = pattern.matcher(content);
        int englishWords = 0;
        while (matcher.find()) {
            englishWords++;
        }

        String chinese = content.replaceAll("[^\\p{IsHan}]", "");
        int chineseChars = chinese.length();

        int wordCount = englishWords + chineseChars;
        double amount = wordCount * 1.5;

        AnalyzeResult result = jobResultRepository.findById(jobId).orElseThrow();
        result.setWordCount(wordCount);
        result.setAmount(amount);
        result.setStatus("DONE");
        jobResultRepository.save(result);

        System.out.println("分析完成: " + jobId);
    }
}
