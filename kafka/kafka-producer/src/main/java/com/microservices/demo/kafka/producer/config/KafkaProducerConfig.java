package com.microservices.demo.kafka.producer.config;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.KafkaProducerConfigData;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig<K extends Serializable, V extends SpecificRecordBase> {

    private final KafkaConfigData kafkaConfigData; // Kafka ile ilgili genel konfigürasyon verilerini içeren sınıf, örneğin Kafka broker adresleri, topic isimleri gibi bilgileri içerebilir.
    private final KafkaProducerConfigData kafkaProducerConfigData; // Kafka producer ile ilgili özel konfigürasyon verilerini içeren sınıf, örneğin key ve value serializer sınıfları, acks gibi bilgileri içerebilir.

    public KafkaProducerConfig(KafkaConfigData configData, KafkaProducerConfigData producerConfigData) {
        this.kafkaConfigData = configData;
        this.kafkaProducerConfigData = producerConfigData;
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        // Kafka producer için gerekli konfigürasyonları içeren bir Map oluşturur. Bu konfigürasyonlar, Kafka producer'ın nasıl çalışacağını belirler. Örneğin, Kafka broker adresleri, key ve value serializer sınıfları, acks gibi bilgileri içerir.
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getKeySerializerClass());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getValueSerializerClass());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfigData.getAcks());
        props.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerConfigData.getCompressionType());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProducerConfigData.getBatchSize() * kafkaProducerConfigData.getBatchSizeBoostFactor());
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigData.getLingerMs());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfigData.getRequestTimeoutMs());
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigData.getRetryCount());

        return props;
    }

    @Bean
    public ProducerFactory<K, V> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs()); // FACTORY DESIGN PATTERN
    }// Producer oluşturabilecek factory nesnesi

    @Bean
    public KafkaTemplate<K, V> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }// KafkaTemplate, Kafka producer'ı kullanarak mesaj göndermek için kullanılan bir sınıftır.
    // KafkaTemplate, Kafka producer'ın sağladığı işlevselliği daha kolay ve daha yüksek seviyede bir API ile sunar.
    // KafkaTemplate,  spring framework tarafından sağlanan bir sınıftır .
}
