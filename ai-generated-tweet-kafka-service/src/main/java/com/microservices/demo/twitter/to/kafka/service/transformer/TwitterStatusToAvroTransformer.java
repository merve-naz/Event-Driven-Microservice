package com.microservices.demo.twitter.to.kafka.service.transformer;


import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;

@Component
public class TwitterStatusToAvroTransformer {
    public TwitterAvroModel getTwitterAvroModelFromTwitterStatus(String  tweetText) {
        // TwitterStatus nesnesini TwitterAvroModel'e dönüştürme işlemi burada yapılır
        // Örneğin, gerekli alanları alarak yeni bir TwitterAvroModel oluşturabilirsiniz
        return TwitterAvroModel.newBuilder()
                .setId(System.currentTimeMillis())
                .setUserId(1L)
                .setText(tweetText)
                .setCreatedAt(System.currentTimeMillis())
                .build();
    } // Bu örnekte, sadece tweetText'i alarak basit bir TwitterAvroModel oluşturulmuştur. Gerçek uygulamada, TwitterStatus nesnesinden daha fazla bilgi alarak daha zengin bir TwitterAvroModel oluşturabilirsiniz.
}
