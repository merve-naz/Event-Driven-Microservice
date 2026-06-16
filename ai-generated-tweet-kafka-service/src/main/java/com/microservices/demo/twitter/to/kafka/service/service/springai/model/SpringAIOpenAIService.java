package com.microservices.demo.twitter.to.kafka.service.service.springai.model;

import com.microservices.demo.config.AIGeneratedTweetToKafkaServiceData;
import com.microservices.demo.twitter.to.kafka.service.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
public class SpringAIOpenAIService  implements AIService{ // AIService arayüzünü implement ediyoruz, böylece bu sınıfın generateTweet() metodunu kullanarak tweet üretebiliriz. Bu, uygulamanın diğer bölümlerinin AI hizmetiyle etkileşim kurmasını sağlar ve farklı AI sağlayıcılarıyla kolayca entegrasyon yapmamıza olanak tanır.
//Provider destegi

    private  final ChatClient chatClient; // Spring AI ChatClient, OpenAI'ye istek göndermek için kullanılır. ChatClient, Spring AI tarafından sağlanan bir arayüzdür ve farklı AI sağlayıcılarıyla entegrasyon sağlar. Bu sayede, OpenAI gibi bir sağlayıcıya kolayca bağlanabiliriz.
    private final AIGeneratedTweetToKafkaServiceData configData;


    @Value("classpath:/templates/tweet-prompt.txt")
    private Resource tweetPrompt;

    public SpringAIOpenAIService(ChatClient chatClient, AIGeneratedTweetToKafkaServiceData configData) {
        this.chatClient = chatClient;
        this.configData = configData;
    }

    @Override
    public TweetResponse generateTweet() {
        BeanOutputConverter<TweetResponse> converter = new BeanOutputConverter(TweetResponse.class);
        log.info("Generating tweet with Spring AI OpenAI Service...");
        PromptTemplate promptTemplate = new PromptTemplate(tweetPrompt);
        Prompt prompt = promptTemplate.create(Map.of(
                configData.getKeywordsPlaceholder().
                        replace("{", "").replace("}", ""),
                String.join(", ", configData.getStreamingDataKeywords()),
                "format", converter.getFormat()
        ));

        TweetResponse modelResponse = chatClient.prompt(prompt).
                options(
                        OpenAiChatOptions.builder()
                        .model(configData.getOpenAi().getModel())
                        .maxTokens(Math.toIntExact(
                                Long.valueOf(configData.getOpenAi().getMaxCompletionTokens()))
                        )
                        .temperature(Double.valueOf(configData.getOpenAi().getTemperature()))
                        .build()).call().entity(TweetResponse.class);


        return modelResponse;
    }

}
