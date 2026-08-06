package com.microservices.demo.reactive.elastic.query.service.business.impl;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.common.transformer.ElasticToResponseModelTransformer;
import com.microservices.demo.reactive.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.reactive.elastic.query.service.business.ReactiveElasticQueryClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class TwitterElasticQueryService implements ElasticQueryService {

    private final ReactiveElasticQueryClient<TwitterIndexModel> reactiveElasticQueryClient;
    private final ElasticToResponseModelTransformer converter;

    public TwitterElasticQueryService(ReactiveElasticQueryClient<TwitterIndexModel> reactiveElasticQueryClient, ElasticToResponseModelTransformer converter) {
        this.reactiveElasticQueryClient = reactiveElasticQueryClient;
        this.converter = converter;
    }

    @Override
    public Flux<ElasticQueryServiceResponseModel> getDocumentsByText(String text) {
       return reactiveElasticQueryClient.getIndexModelByText(text)
                    .map(converter::getResponseModel);//Reactive'de buna lazy execution (tembel çalışma) denir.
    }
}
