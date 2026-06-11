package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "elastic-config")
public class ElasticConfigData {
    // Elasticsearch'te kullanılacak index adı
    private String indexName;

    // Elasticsearch sunucusunun adresi
    // Örn: localhost:9200
    private String connectionUrl;

    // Elasticsearch'e bağlanırken beklenecek maksimum süre (milisaniye)
    // Bağlantı kurulamazsa timeout hatası alınır.
    private Integer connectTimeoutMs;

    // Bağlantı kurulduktan sonra veri alışverişi için beklenecek maksimum süre
    // Uzun süren sorgularda kullanılır.
    private Integer socketTimeoutMs;
}
