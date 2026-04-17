package com.microservices.demo.twitter.to.kafka.service.service.openai;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.microservices.demo.twitter.to.kafka.service.config.AIGeneratedTweetToKafkaServiceData;
//import com.microservices.demo.twitter.to.kafka.service.exception.AIGeneratedTweetToKafkaException;
//import com.microservices.demo.twitter.to.kafka.service.service.AIService;
//import com.microservices.demo.twitter.to.kafka.service.service.openai.model.OpenAIRequest;
//import com.microservices.demo.twitter.to.kafka.service.service.openai.model.OpenAIResponse;
//import org.apache.hc.client5.http.classic.methods.HttpPost;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
//import org.apache.hc.client5.http.impl.classic.HttpClients;
//import org.apache.hc.core5.http.HttpHeaders;
//import org.apache.hc.core5.http.io.entity.EntityUtils;
//import org.apache.hc.core5.http.io.entity.StringEntity;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Conditional;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;

//@Service
//@ConditionalOnProperty(name = "twitter-to-kafka-service.ai-service", havingValue = "OpenAI" )
/*public class OpenAIService implements AIService {

    private final AIGeneratedTweetToKafkaServiceData configData;
    private final ObjectMapper objectMapper;

    public OpenAIService(AIGeneratedTweetToKafkaServiceData configData, ObjectMapper objectMapper) {
        this.configData = configData;
        this.objectMapper = objectMapper;
    }

    @Override
    public String generateTweet() throws AIGeneratedTweetToKafkaException {
        // Prompt içindeki placeholder'ı (örneğin {keywords}) gerçek keyword'ler ile değiştiriyoruz
        String prompt = configData.getPrompt().replace(
                configData.getKeywordsPlaceholder(), String.join(", ", configData.getStreamingDataKeywords())
        );
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost request = getRequest(prompt);
            String response = httpClient.execute(request, resp -> EntityUtils.toString(resp.getEntity()));
            return parseResponse(response);

        } catch (Exception e) {
            throw new AIGeneratedTweetToKafkaException("Error while generating tweet from OpenAI", e);
        }

    }

    private HttpPost getRequest(String prompt) throws JsonProcessingException {
        // REUEST HEADER KISMINI OLUŞTURUYORUZ
        HttpPost request = new HttpPost(configData.getOpenAi().getUrl()); // burada  requestimizi oluşturuyoruz
        request.addHeader(HttpHeaders.CONTENT_TYPE, configData.getOpenAi().getContentType());
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + configData.getOpenAi().getApiKey());
        // reuqest body
        OpenAIRequest openAIRequest = OpenAIRequest.builder()
                .model(configData.getOpenAi().getModel())
                .max_completion_tokens(Integer.parseInt(configData.getOpenAi().getMaxCompletionTokens()))
                .temperature(Double.parseDouble(configData.getOpenAi().getTemperature()))
                .messages(configData.getOpenAi().getMessages().stream().map(message ->
                        OpenAIRequest.Message.builder()
                                .role(message.getRole())
                                .content(List.<OpenAIRequest.Content>of(
                                        OpenAIRequest.Content.builder()
                                                .text(prompt)
                                                .type(message.getContent().get(0).getType())
                                                .build())
                                )
                                .build()).toList())
                .build();

        request.setEntity(new StringEntity(objectMapper.writeValueAsString(openAIRequest)));

        return request;


    }

    private String parseResponse(String response) throws JsonProcessingException {
        OpenAIResponse openAIResponse = objectMapper.readValue(response, OpenAIResponse.class);
        return openAIResponse.getChoices().stream()
                .map(choice -> choice.getMessage().getContent())
                .collect(Collectors.joining("\n"));
    }
}*/
