package com.microservices.demo.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData {
    private String bootstrapServers;
    private String schemaRegistryUrlKey;
    private String schemaRegistryUrl;
    private String topicName;
    private List<String> topicNamesToCreate;
    private Short replicationFactor;
    private Integer numPartitions;

} // Başka yerde KafkaConfigData sınıfı tanımlanmış olabilir, bu nedenle KafkaConfigData sınıfının içeriği ve özellikleri farklı olabilir. Ancak genel olarak, KafkaConfigData sınıfı Kafka ile ilgili yapılandırma verilerini içeren bir sınıftır ve genellikle Spring Boot uygulamalarında @ConfigurationProperties anotasyonu ile kullanılır.
// Bu sınıf, Kafka bağlantısı, topic oluşturma, schema registry gibi konfigürasyonları içerebilir.
