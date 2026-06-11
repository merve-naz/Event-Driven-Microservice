package com.microservices.demo.twitter.to.kafka.service.runner;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.config.service.KafkaProducer;
import com.microservices.demo.twitter.to.kafka.service.service.AIService;
import com.microservices.demo.twitter.to.kafka.service.transformer.TwitterStatusToAvroTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AIStreamRunner implements  Runnable{

    private  final AIService aiService;
    private final KafkaProducer kafkaProducer;
    private final KafkaConfigData kafkaConfigData;
    private final TwitterStatusToAvroTransformer transformer;

    public AIStreamRunner(AIService aiService, KafkaProducer kafkaProducer, KafkaConfigData kafkaConfigData, TwitterStatusToAvroTransformer transformer) {
        this.aiService = aiService;
        this.kafkaProducer = kafkaProducer;
        this.kafkaConfigData = kafkaConfigData;

        this.transformer = transformer;
    }

    @Override
    public void run() {
        while (true) { // Sürekli tweet üretmesi için döngü ekleyelim
            try {
                String generatedTweet = aiService.generateTweet();
                log.info("Generated Tweet: {}", generatedTweet);

                TwitterAvroModel model = transformer.getTwitterAvroModelFromTwitterStatus(generatedTweet);

                kafkaProducer.send(kafkaConfigData.getTopicName(), String.valueOf(model.getUserId()), model);
                log.info("Tweet sent to kafka");

                Thread.sleep(5000); // 5 saniyede bir tweet üret
            } catch (Exception e) {
                log.error("Error in AI stream: ", e);
                break;
            }
        }
    }
}
