package com.microservices.demo.twitter.to.kafka.service.init.impl;

import com.microservices.demo.kafka.admin.client.KafkaAdminClient;
import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.twitter.to.kafka.service.init.StreamInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamInitializer implements StreamInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamInitializer.class);
    private final KafkaConfigData kafkaConfigData;
    private final KafkaAdminClient kafkaAdminClient;

    public KafkaStreamInitializer(KafkaConfigData kafkaConfigData, KafkaAdminClient kafkaAdminClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaAdminClient = kafkaAdminClient;
    }

    @Override
    public boolean init() {
        // Kafka stream başlatma işlemleri burada yapılır
        // Örneğin, Kafka topic'lerini oluşturma, bağlantıları açma vb.

        LOG.info("Initializing Kafka Stream...");
        kafkaAdminClient.createTopics(); // Kafka topic'lerini oluşturur
        kafkaAdminClient.checkSchemaRegistry(); // Schema Registry'nin erişilebilir olduğunu kontrol eder


        return true; // Başarılı bir şekilde başlatıldığını varsayıyoruz
    }
}