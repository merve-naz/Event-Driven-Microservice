package com.microservices.demo.reactive.elastic.query.service.business.impl;

import com.microservices.demo.config.ElasticQueryServiceConfigData;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.reactive.elastic.query.service.business.ReactiveElasticQueryClient;
import com.microservices.demo.reactive.elastic.query.service.repository.ElasticQueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class TwitterReactiveElasticQueryClient
        implements ReactiveElasticQueryClient<TwitterIndexModel> {
// "Client" burada HTTP client, Elasticsearch client veya başka bir dış servisi çağıran bileşen anlamında kullanılır.
    private static final Logger LOG =
            LoggerFactory.getLogger(TwitterReactiveElasticQueryClient.class);

    private final ElasticQueryRepository elasticQueryRepository;

    private final ElasticQueryServiceConfigData elasticQueryServiceConfigData;

    public TwitterReactiveElasticQueryClient(
            ElasticQueryRepository elasticRepository,
            ElasticQueryServiceConfigData configData) {

        this.elasticQueryRepository = elasticRepository;
        this.elasticQueryServiceConfigData = configData;
    }

    @Override
    public Flux<TwitterIndexModel> getIndexModelByText(String text) {
        LOG.info("Getting data from elasticsearch for text {}", text);

        return elasticQueryRepository
                .findByText(text)
                .delayElements( //Flux'ın içindeki her elemanı, belirtilen süre kadar geciktirerek yayınlıyor.
                        Duration.ofMillis(
                                elasticQueryServiceConfigData.getBackPressureDelayMs()));
    }
}