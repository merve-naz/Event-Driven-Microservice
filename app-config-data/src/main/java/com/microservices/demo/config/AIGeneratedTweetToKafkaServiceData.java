package com.microservices.demo.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "twitter-to-kafka-service")
public class AIGeneratedTweetToKafkaServiceData {
    private List<String> streamingDataKeywords;
    private Long scheduleDurationMs;
    private String prompt;
    private String keywordsPlaceholder;
    private OpenAI openAi;


    @Data
    public static class OpenAI {
        private String url;
        private String apiKey;
        private String model;
        private String contentType;
        private String maxCompletionTokens;
        private String temperature;
        private List<Message> messages;

    }

    @Data
    public static class Message {
        private String role;
        private List<Content> content;
    }


    @Data
    public static class Content {
        private String text;
        private String type;
    }
}