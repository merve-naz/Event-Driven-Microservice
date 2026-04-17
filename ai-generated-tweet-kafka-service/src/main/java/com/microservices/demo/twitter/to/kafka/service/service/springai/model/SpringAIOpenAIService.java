package com.microservices.demo.twitter.to.kafka.service.service.springai.model;

import com.microservices.demo.twitter.to.kafka.service.config.AIGeneratedTweetToKafkaServiceData;
import com.microservices.demo.twitter.to.kafka.service.service.AIService;
import com.microservices.demo.twitter.to.kafka.service.service.openai.OpenAIService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
public class SpringAIOpenAIService  implements AIService{

    private  final ChatClient chatClient;
    private final AIGeneratedTweetToKafkaServiceData configData;


    @Value("classpath:/templates/tweet-prompt.txt")
    private Resource tweetPrompt;

    public SpringAIOpenAIService(ChatClient chatClient, AIGeneratedTweetToKafkaServiceData configData) {
        this.chatClient = chatClient;
        this.configData = configData;
    }

    @Override
    public String generateTweet() {
        BeanOutputConverter<TweetResponse> converter = new BeanOutputConverter(TweetResponse.class);
        log.info("Generating tweet with Spring AI OpenAI Service...");
        log.info("converter format: {}", converter.getFormat());
        PromptTemplate promptTemplate = new PromptTemplate(tweetPrompt);
        Prompt prompt = promptTemplate.create(Map.of(
                configData.getKeywordsPlaceholder().replace("{", "").replace("}", ""),
                String.join(", ", configData.getStreamingDataKeywords()),
                "format", converter.getFormat()
        ));

        String modelResponse = chatClient.prompt(prompt).call().content();
        log.info("Generated tweet: {}", modelResponse);
        return modelResponse;





    }

}
