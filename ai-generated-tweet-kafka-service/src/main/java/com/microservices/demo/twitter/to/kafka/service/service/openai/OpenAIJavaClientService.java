//package com.microservices.demo.twitter.to.kafka.service.service.openai;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.microservices.demo.twitter.to.kafka.service.config.AIGeneratedTweetToKafkaServiceData;
//import com.microservices.demo.twitter.to.kafka.service.service.AIService;
//import com.openai.client.OpenAIClient;
//import com.openai.client.okhttp.OpenAIOkHttpClient;
//import com.openai.models.chat.completions.ChatCompletion;
//import com.openai.models.chat.completions.ChatCompletionCreateParams;
//import com.openai.models.chat.completions.ChatCompletionMessage;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Service;
//
//import java.util.List;


//@Slf4j
//@Service
//@ConditionalOnProperty(name = "twitter-to-kafka-service.ai-service", havingValue = "OpenAI-JavaClient" )
//public class OpenAIJavaClientService implements AIService {
//
//    private final AIGeneratedTweetToKafkaServiceData configData;
//
//
//    public OpenAIJavaClientService(AIGeneratedTweetToKafkaServiceData configData) {
//        this.configData = configData;
//    }
//
//    @Override
//    public String generateTweet() {
//        log.info("Generateing tweet with OpenAI Java Client...");
//        String prompt = configData.getPrompt().replace(
//                configData.getKeywordsPlaceholder(), String.join(", ", configData.getStreamingDataKeywords())
//        );
//        OpenAIClient client = OpenAIOkHttpClient.fromEnv();
//        ChatCompletionCreateParams.Builder createParams = ChatCompletionCreateParams.builder().
//                 model(configData.getOpenAi().getModel())
//                .addDeveloperMessage("You are helping  me to create a tweet content based on the following keywords: " )
//                .maxCompletionTokens(Long.valueOf(configData.getOpenAi().getMaxCompletionTokens()))
//                .temperature(Double.valueOf(configData.getOpenAi().getTemperature()))
//                .addUserMessage(prompt);
//
//        // 3. OpenAI'ye isteği gönder + cevabı al
//        List<ChatCompletionMessage> messages = client.chat()
//                .completions()
//                .create(createParams.build())
//                .choices()
//                .stream()
//                .map(ChatCompletion.Choice::message)
//                .toList();
//
//        // 4. İlk cevabı döndür
//        return messages.getFirst().content().get();
//
//    }
//
//
//}
