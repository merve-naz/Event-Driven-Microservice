package com.microservices.demo.elastic.config;

import java.time.Duration;


import com.microservices.demo.config.ElasticConfigData;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

//Bu sınıfla birlikte arka planda tam olarak 2 kritik nesne oluşturmuş oldun:
//
//ElasticsearchClient: Sunucuyla fiziksel köprüyü kuran bağlantı motoru.
//
//ElasticsearchTemplate: Senin Java nesnelerini otomatik JSON'a çeviren akıllı sorgu motoru.
@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    private final ElasticConfigData elasticConfigData;

    public ElasticsearchConfig(ElasticConfigData configData) {
        this.elasticConfigData = configData;
    }

    @Override
    public ClientConfiguration clientConfiguration() {
        // http:// veya https:// protokol eklerini temizleyerek adresi alır
        String cleanedUrl = elasticConfigData.getConnectionUrl()
                .replace("http://", "")
                .replace("https://", "");

        return ClientConfiguration.builder()
                .connectedTo(cleanedUrl)
                // Eski milisaniye int değerleri yerine modern java.time.Duration kullanıyoruz
                .withConnectTimeout(Duration.ofMillis(elasticConfigData.getConnectTimeoutMs()))
                .withSocketTimeout(Duration.ofMillis(elasticConfigData.getSocketTimeoutMs()))
                .build();
    }
}
/**
 *  (ARKA PLANDA DÖNEN GİZLİ BÜYÜ):
 * * Biz bu sınıfta "ElasticsearchClient" (Bağlantı Motoru) veya "ElasticsearchTemplate" (Sorgu Motoru)  (  elasticsearchOperations ımplamatasıyonı) ÜRETMEDİK.
 * * Peki onlar nasıl oluştu?
 * Biz bu sınıftan dışarıya bu doldurduğumuz "Tarif Paketini" (ClientConfiguration) fırlattığımız an,
 * üst sınıfımız olan "ElasticsearchConfiguration" (yani Fabrika) bu paketi havada yakalar.
 * * İçindeki adresi ve timeout sürelerini tek tek okur. O bilgilere bakarak arka planda:
 * 1. Gerçek, canlı, istek atabilen "ElasticsearchClient" nesnesini otomatik üretir ve hafızaya koyar.
 * 2. save(), findById() gibi metodları çalıştıran "ElasticsearchTemplate" nesnesini otomatik üretir ve hafızaya koyar.
 * * Yani biz sadece planı çizdik (ClientConfiguration), üst sınıf ise binayı inşa etti (Client ve Template nesnelerini kurdu).
 */