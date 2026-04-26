package com.microservices.demo.twitter.to.kafka.service.runner;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.config.service.KafkaProducer;
import com.microservices.demo.twitter.to.kafka.service.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AIStreamRunner implements  Runnable{

    private  final AIService aiService;
    private final KafkaProducer kafkaProducer;
    private final KafkaConfigData kafkaConfigData;

    public AIStreamRunner(AIService aiService, KafkaProducer kafkaProducer, KafkaConfigData kafkaConfigData) {
        this.aiService = aiService;
        this.kafkaProducer = kafkaProducer;
        this.kafkaConfigData = kafkaConfigData;
    }

    @Override
 public void run (){
        String  generatedTweet = aiService.generateTweet();

        log.info("Generated Tweet: {}", generatedTweet);
        // Avro modele çevir
        TwitterAvroModel model =
                TwitterAvroModel.newBuilder()
                        .setId(System.currentTimeMillis())
                        .setUserId(1L)
                        .setText(generatedTweet)
                        .setCreatedAt(System.currentTimeMillis())
                        .build();
        // Kafka'ya gönder
        kafkaProducer.send(
                kafkaConfigData.getTopicName(),
                model.getUserId(),
                model
        );
//        partition = hash(key) % partitionCount
//        Aynı key tekrar gelirse: userId = 15 yine aynı hash çıkar.
//        Yani yine aynı partition’a gider. Bu da ordering sağlar.
        log.info("Tweet sent to kafka");


    }
}
