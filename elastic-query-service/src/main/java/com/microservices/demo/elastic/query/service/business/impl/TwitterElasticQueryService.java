package com.microservices.demo.elastic.query.service.business.impl;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.client.service.ElasticQueryClient;
import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.assembler.ElasticQueryServiceResponseModelAssembler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwitterElasticQueryService implements ElasticQueryService {

    private final ElasticQueryServiceResponseModelAssembler assembler;
    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    public TwitterElasticQueryService(ElasticQueryServiceResponseModelAssembler assembler, ElasticQueryClient elasticQueryClient) {
        this.assembler = assembler;
        this.elasticQueryClient = elasticQueryClient;
    }

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        return assembler.toModel(elasticQueryClient.getIndexModelById(id));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getDocumentsByText(String text) {
        return  assembler.toModels(elasticQueryClient.getIndexModelsByText(text));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
      return  assembler.toModels(elasticQueryClient.getAllIndexModels());
    }
}
