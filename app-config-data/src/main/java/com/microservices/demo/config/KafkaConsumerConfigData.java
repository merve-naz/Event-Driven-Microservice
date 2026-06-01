package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-consumer-config")
public class KafkaConsumerConfigData {

    // Mesajın key kısmını deserialize edecek sınıf
    // Örn: StringDeserializer
    private String keyDeserializer;

    // Mesajın value kısmını deserialize edecek sınıf
    // Örn: KafkaAvroDeserializer
    private String valueDeserializer;

    // Consumer Group adı
    // Offsetler bu group adına göre tutulur
    private String consumerGroupId;

    // Offset bulunamazsa ne yapılacağını belirler
    // earliest -> baştan oku
    // latest -> sadece yeni gelenleri oku
    private String autoOffsetReset;

    // Avro okuyucu ayarının property adı
    // Genelde "specific.avro.reader"
    private String specificAvroReaderKey;

    // Avro mesajları GenericRecord yerine
    // TwitterAvroModel gibi sınıflara dönüştürsün mü?
    // Genelde true olur
    private String specificAvroReader;

    // Mesajları tek tek mi yoksa toplu mu okuyacağız?
    // true -> List<Message>
    // false -> Message
    private Boolean batchListener;

    // Uygulama açılınca consumer otomatik başlasın mı?
    private Boolean autoStartup;

    // Kaç consumer thread oluşturulacak?
    // Partition sayısı uygunsa paralel tüketim sağlar
    private Integer concurrencyLevel;

    // Kafka'nın consumer'ın öldüğünü anlaması için beklediği süre
    // Bu süre içinde heartbeat gelmezse consumer düşmüş kabul edilir
    private Integer sessionTimeoutMs;

    // Consumer'ın Kafka'ya
    // "Ben hala yaşıyorum" demesi için gönderdiği heartbeat süresi
    private Integer heartbeatIntervalMs;

    // İki poll çağrısı arasında izin verilen maksimum süre
    // Çok uzun sürerse Kafka consumer'ı takılmış sanabilir
    private Integer maxPollIntervalMs;

    // Bir poll() çağrısında en fazla kaç mesaj alınsın
    private Integer maxPollRecords;

    // Bir partition'dan tek seferde çekilebilecek maksimum veri miktarı
    // Örn: 1 MB
    private Integer maxPartitionFetchBytesDefault;

    private Integer  maxPartitionFetchBytesBoostFactor;

    private  Integer pollTimeoutMs;
}