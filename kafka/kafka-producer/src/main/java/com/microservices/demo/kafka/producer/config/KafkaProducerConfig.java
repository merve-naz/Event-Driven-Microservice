package com.microservices.demo.kafka.producer.config;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.KafkaProducerConfigData;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class KafkaProducerConfig<K extends Serializable, V extends SpecificRecordBase> {
// SpecificRecord = Avro schema’dan üretilmiş Java class demektir.
// Serializable Java’da bir nesnenin byte stream’e çevrilebilmesini işaretleyen marker interface’tir.

    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducerConfigData kafkaProducerConfigData;

    public KafkaProducerConfig(KafkaConfigData configData, KafkaProducerConfigData producerConfigData) {
        this.kafkaConfigData = configData;
        this.kafkaProducerConfigData = producerConfigData;
    }

    // Bu method bean yapıldı çünkü producer config nesnesi Spring tarafından tek kez oluşturulsun,
    // yönetilsin ve diğer bean'lerde tekrar kullanılabilsin.
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();

        // Kafka producer configlerini tutan hazır config class’ı.
        // ProducerConfig class’ında Kafka producer configleri için hazır static değişkenler var.

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        // Kafka broker adresleri , Producer hangi Kafka cluster'a bağlanacak.

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getKeySerializerClass());

        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getValueSerializerClass());

        props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfigData.getAcks());
        // Producer’ın mesaj gönderirken Kafka broker’dan ne tür bir onay bekleyeceği. (acks=all → tüm replikaların mesajı alması gerekiyor)

        //Schema Registry ayarı Kafka’nın resmi producer config’i değil.
        // Bu yüzden ProducerConfig içinde yok.
        props.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());

        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerConfigData.getCompressionType());

        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProducerConfigData.getBatchSize() * kafkaProducerConfigData.getBatchSizeBoostFactor());
        // Batch size arttırma katsayısı ile batch size’ı boost ediyoruz. Daha fazla batch oluşabilir, performans artar.
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigData.getLingerMs());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfigData.getRequestTimeoutMs());
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigData.getRetryCount());

        return props;
    }


    //    KafkaTemplate'in mesaj gönderebilmesi için arkada gerçek Kafka Producer nesnelerine ihtiyacı
    //    vardır. ProducerFactory, bu Producer nesnelerinin nasıl oluşturulacağını,
    //    hangi ayarlara (config) sahip olacağını belirleyen "imalathanedir".
    @Bean
    public ProducerFactory<K, V> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs()); // FACTORY DESIGN PATTERN
    }

    // ProducerFactory: Kafka'ya mesaj atacak "motoru" (Producer nesnesini) üreten fabrikadır.
// KafkaTemplate: Bu motoru kullanan "şofördür"; hata yönetimi ve bağlantı gibi karmaşık işleri
// otomatik halleder, bizim sadece .send() dememizi sağlar.
    @Bean
    public KafkaTemplate<K, V> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


}
