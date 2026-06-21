package com.microservices.demo.elastic.query.client.service.impl;

import com.microservices.demo.config.ElasticConfigData;
import com.microservices.demo.config.ElasticQueryConfigData;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.client.service.ElasticQueryClient;
import com.microservices.demo.elastic.query.client.exception.ElasticQueryClientException;
import com.microservices.demo.elastic.query.client.util.ElasticQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.List;
import java.util.stream.Collectors;

public class TwitterElasticQueryClient implements ElasticQueryClient<TwitterIndexModel> {


     private static final Logger logger = LoggerFactory.getLogger(TwitterElasticQueryClient.class);
        private final ElasticQueryConfigData elasticQueryConfigData;
        private final ElasticConfigData elasticConfigData;
        private final ElasticQueryUtil elasticQueryUtil;
        private final ElasticsearchOperations  elasticsearchOperations;

    public TwitterElasticQueryClient(ElasticQueryConfigData elasticQueryConfigData, ElasticConfigData elasticConfigData, ElasticQueryUtil elasticQueryUtil, ElasticsearchOperations elasticsearchOperations) {
        this.elasticQueryConfigData = elasticQueryConfigData;
        this.elasticConfigData = elasticConfigData;
        this.elasticQueryUtil = elasticQueryUtil;
        this.elasticsearchOperations = elasticsearchOperations;
    }



    @Override
        public TwitterIndexModel getIndexModelById(String id) {
        Query query = elasticQueryUtil.getSearchQueryById(id); // query oluşturuyoruz
        // 2. ADIM: ElasticsearchOperations motorunu tetikliyoruz.
        // searchOne metodu tek bir kayıt döneceğini bildiğimiz durumlarda kullanılır.
        // Parametre olarak: Sorguyu, maplenecek Java sınıfını ve
        // .yml dosyasından okunan indeks adını (IndexCoordinates) veriyoruz.
        SearchHit<TwitterIndexModel> searchResult = elasticsearchOperations.searchOne(
                query,
                TwitterIndexModel.class,
                IndexCoordinates.of(elasticConfigData.getIndexName())
        );
        if (searchResult == null) {
            logger.error("No document found at elasticsearch with id {}", id);

            throw new ElasticQueryClientException(
                    "No document found at elasticsearch with id " + id);
        }

        logger.info("Document with id {} retrieved successfully",
                searchResult.getId());

          return searchResult.getContent();
    }


        @Override
        public List<TwitterIndexModel> getIndexModelsByText(String text) {
            Query query = elasticQueryUtil.getSearchQueryByFieldText(
                    elasticQueryConfigData.getTextField(),
                    text
            );
            return search(
                    query,
                    "{} of documents with text {} retrieved successfully",
                    text
            );
        }

    @Override
        public java.util.List<TwitterIndexModel> getAllIndexModels() {
            Query query = elasticQueryUtil.getSearchQueryForAll();
            return search(
                    query,
                    "All {} documents retrieved successfully"
            );
        }



    private List<TwitterIndexModel> search(Query query,String logMessage, Object... logParams) {
        SearchHits<TwitterIndexModel> searchResult = elasticsearchOperations.search(
                query,
                TwitterIndexModel.class,
                IndexCoordinates.of(elasticConfigData.getIndexName()));

        logger.info(
                logMessage,
                searchResult.getTotalHits(),
                logParams
        );


        return searchResult.get().map(
                SearchHit::getContent
        ).collect(Collectors.toList());
    }

}
