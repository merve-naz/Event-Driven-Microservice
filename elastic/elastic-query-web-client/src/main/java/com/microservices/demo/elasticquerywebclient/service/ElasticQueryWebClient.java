package com.microservices.demo.elasticquerywebclient.service;

import com.microservices.demo.elasticquerywebclient.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elasticquerywebclient.model.ElasticQueryWebClientResponseModel;

import java.util.List;

public interface ElasticQueryWebClient {
    List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel);
}
