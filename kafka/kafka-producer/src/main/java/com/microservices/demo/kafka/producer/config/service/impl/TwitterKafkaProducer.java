package com.microservices.demo.kafka.producer.config.service.impl;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.config.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


//KafkaTemplate düşük seviye gönderim aracıdır.
// onun üstüne uygulamaya özel producer servis katmanı oluşturalım
@Component
public class TwitterKafkaProducer implements KafkaProducer<String, TwitterAvroModel> {

    private final KafkaTemplate<String, TwitterAvroModel> kafkaTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaProducer.class);

    public TwitterKafkaProducer(KafkaTemplate<String, TwitterAvroModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topicName, String key, TwitterAvroModel value) {
        LOG.info("Sending message='{}' to topic='{}'", value, topicName);
        // KafkaTemplate ile mesaj gönderme işlemi burada yapılacak
        CompletableFuture<SendResult<String, TwitterAvroModel>> kafkaResultFuture =
                kafkaTemplate.send(topicName, key, value);

        callback(topicName, value, kafkaResultFuture);

    }

    @PreDestroy
    public void close() {

        // Spring bean kapanırken otomatik çalışır
        // Uygulama shutdown olurken resource temizliği yapılır

        if (kafkaTemplate != null) {

            LOG.info("Closing kafka producer!");

            // KafkaTemplate içindeki producer bağlantılarını kapatır
            // buffer flush edilir
            // network/resource cleanup yapılır
            kafkaTemplate.destroy();
        }
    }


    private static void callback(String topicName, TwitterAvroModel value, CompletableFuture<SendResult<String, TwitterAvroModel>> kafkaResultFuture) {
        kafkaResultFuture.whenComplete((result, ex) -> {
            if (ex == null) {
                // result kafka'nın bize gönderdiği metadata bilgilerini içerir
                RecordMetadata metadata = result.getRecordMetadata();
                LOG.info(
                        "Received new metadata. Topic: {}; Partition {}; Offset: {}, Timestamp: {}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp()
                );
            } else {
                if (ex != null) {
                    LOG.error(
                            "Error while sending message {} to topic {}",
                            value,
                            topicName,
                            ex
                    );
                    return;
                }
            }
        });
    }
}
