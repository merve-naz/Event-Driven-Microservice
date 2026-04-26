package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-producer-config")
public class KafkaProducerConfigData {

    // Kafka producer key serializer class
    // Mesaj key kısmı nasıl byte[] olacak onu belirler
    private String keySerializerClass;

    // Kafka producer value serializer class
    // Mesaj payload kısmı nasıl çevrilecek (Json / Avro vs)
    private String valueSerializerClass;

    // Mesaj sıkıştırma tipi (gzip, snappy, lz4, zstd)
    // Trafiği azaltır
    private String compressionType;

    // Broker'ın kaç onayı sonrası mesaj başarılı sayılsın
    // all / 1 / 0
    private String acks;

    // Batch içine kaç byte veri toplansın
    // Toplu gönderim performans sağlar
    private Integer batchSize;

    // Batch size arttırma katsayısı
    // Custom kullanım için eklenmiş
    private Integer batchSizeBoostFactor;

    // Yeni mesaj beklemek için producer ne kadar beklesin
    // Daha fazla batch oluşabilir
    private Integer lingerMs;

    // Broker response bekleme timeout süresi
    private Integer requestTimeoutMs;

    // Hata olursa kaç kez retry yapılsın
    private Integer retryCount;
}