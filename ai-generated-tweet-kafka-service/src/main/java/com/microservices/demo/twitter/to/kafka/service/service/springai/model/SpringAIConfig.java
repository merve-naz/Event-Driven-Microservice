package com.microservices.demo.twitter.to.kafka.service.service.springai.model;

import com.microservices.demo.twitter.to.kafka.service.service.openai.model.OpenAIRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAIConfig {
    //  Spring'ın olusturdugu OpenAiChatModel nesnesini kullanarak ChatClient nesnesi olusturuyoruz.
    //  Bu sayede OpenAI ile kolayca iletişim kurabiliriz.
    // OpenAiChatModel NESNESI OTAMATIK OLUŞTURULUR VE CONFIGURATION DOSYASINDA TANIMLANAN PROPERTIES DEGERLERINI KULLANIR.

    @Bean
    ChatClient openAIchatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel).build();
    }


}
