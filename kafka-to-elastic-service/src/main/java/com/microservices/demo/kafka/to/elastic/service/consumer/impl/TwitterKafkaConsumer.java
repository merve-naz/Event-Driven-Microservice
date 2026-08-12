package com.microservices.demo.kafka.to.elastic.service.consumer.impl;


import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.KafkaConsumerConfigData;
import com.microservices.demo.elastic.index.client.service.ElasticIndexClient;
import com.microservices.demo.kafka.admin.client.KafkaAdminClient;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.to.elastic.service.consumer.KafkaConsumer;
import com.microservices.demo.kafka.to.elastic.service.transformer.AvroToElasticModelTransformer;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TwitterKafkaConsumer
        implements KafkaConsumer<Long, TwitterAvroModel> {

    private static final Logger LOG =
            LoggerFactory.getLogger(TwitterKafkaConsumer.class);

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaConfigData kafkaConfigData;
    private final KafkaAdminClient kafkaAdminClient;
    private final ElasticIndexClient<TwitterIndexModel> elasticIndexClient;
    private final KafkaConsumerConfigData kafkaConsumerConfigData;
    private final AvroToElasticModelTransformer avroToElasticModelTransformer;

    public TwitterKafkaConsumer(
            KafkaAdminClient kafkaAdminClient,
            KafkaListenerEndpointRegistry listenerEndpointRegistry,
            KafkaConfigData kafkaConfigData, ElasticIndexClient<TwitterIndexModel> elasticIndexClient, KafkaConsumerConfigData kafkaConsumerConfigData, AvroToElasticModelTransformer avroToElasticModelTransformer) {

        this.kafkaAdminClient = kafkaAdminClient;
        this.kafkaListenerEndpointRegistry = listenerEndpointRegistry;
        this.kafkaConfigData = kafkaConfigData;
        this.elasticIndexClient = elasticIndexClient;
        this.kafkaConsumerConfigData = kafkaConsumerConfigData;
        this.avroToElasticModelTransformer = avroToElasticModelTransformer;
    }

    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {

        LOG.info("Bootstrap servers: {}",
                kafkaConfigData.getBootstrapServers());

        kafkaAdminClient.checkSchemaRegistry();
        kafkaAdminClient.checkTopicsCreated();

        LOG.info(
                "Topics with name {} is ready for operations!",
                kafkaConfigData.getTopicNamesToCreate().toArray()
        );
// KafkaListenerEndpointRegistry, @KafkaListener ile oluşturulan listener container'larını yönetir.
        Objects.requireNonNull(kafkaListenerEndpointRegistry).
                getListenerContainer(kafkaConsumerConfigData.getConsumerGroupId()).start();
    }

    @Override
    @KafkaListener(
            id = "${kafka-consumer-config.consumer-group-id}",
            topics = "${kafka-config.topic-name}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void receive(
            @Payload List<TwitterAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_KEY) List<Integer> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets) {


        LOG.info(
                "{} number of message received with keys {}, partitions {} and offsets {}, sending it to elastic: Thread id {}",
                messages.size(),
                keys,
                partitions,
                offsets,
                Thread.currentThread().getId()
        );
        List<TwitterIndexModel> twitterIndexModels = avroToElasticModelTransformer.getElasticModels(messages);
        List<String> documentsIds = elasticIndexClient.save(twitterIndexModels);

        LOG.info("Documents saved to elasticsearch with ids {}", documentsIds.toArray());

    }
}