package com.microservices.demo.elastic.index.client.service.impl;

import com.microservices.demo.config.ElasticConfigData;
import com.microservices.demo.elastic.index.client.service.ElasticIndexClient;
import com.microservices.demo.elastic.index.client.util.ElasticIndexUtil;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Spring bu sınıfı bean olarak oluşturur.
public class TwitterElasticIndexClient implements ElasticIndexClient<TwitterIndexModel> {

    // Log basmak için kullanılır.
    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticIndexClient.class);


    private final ElasticConfigData elasticConfigData;

    // Elasticsearch ile işlem yapan Spring Data nesnesi.
    // bulkIndex, search, save gibi işlemleri sağlar.
    private final ElasticsearchOperations elasticsearchOperations;

    // TwitterIndexModel -> IndexQuery dönüşümünü yapan yardımcı sınıf.
    private final ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil;

    // Constructor Injection
    // Spring bu bağımlılıkları otomatik olarak verir.
    public TwitterElasticIndexClient(
            ElasticConfigData configData,
            ElasticsearchOperations elasticOperations,
            ElasticIndexUtil<TwitterIndexModel> indexUtil) {

        this.elasticConfigData = configData;
        this.elasticsearchOperations = elasticOperations;
        this.elasticIndexUtil = indexUtil;
    }

    @Override
    public List<String> save(
            List<TwitterIndexModel> documents) {

        // TwitterIndexModel listesini
        // Elasticsearch'in istediği IndexQuery listesine çevir.
        List<IndexQuery> indexQueries =
                elasticIndexUtil.getIndexQueries(documents);

        // Tüm dokümanları tek seferde Elasticsearch'e kaydet.
        // Dönen değer kaydedilen dokümanların id listesidir.
        List<IndexedObjectInformation> documentIds =
                elasticsearchOperations.bulkIndex(
                        indexQueries,
                        IndexCoordinates.of(
                                elasticConfigData.getIndexName()
                        )
                );

        // Başarı logu bas.
        LOG.info(
                "Documents indexed successfully with type: {} and ids: {}",
                TwitterIndexModel.class.getName(),
                documentIds
        );
//        Spring Data Elasticsearch'ün index adını temsil eden nesnesidir
        // Kaydedilen doküman id'lerini geri döndür.
        return documentIds.stream()
                .map(IndexedObjectInformation::id)
                .toList();
    }
}