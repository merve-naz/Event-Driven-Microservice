package com.microservices.demo.twitter.to.kafka.service.service.openai.model;

import lombok.Data;

import java.awt.*;
import java.util.List;

@Data

public class OpenAIResponse {

    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;
    }

    @Data
    public static class Message {
        private String content;
    }
}
