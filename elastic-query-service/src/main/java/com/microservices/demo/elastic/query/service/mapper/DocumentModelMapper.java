package com.microservices.demo.elastic.query.service.mapper;

import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import org.springframework.stereotype.Component;

@Component
public class DocumentModelMapper {

    public ElasticQueryServiceResponseModelV2 toV2Model (ElasticQueryServiceResponseModel responseModel) {
        ElasticQueryServiceResponseModelV2 model =  ElasticQueryServiceResponseModelV2.builder()
                .id(Long.valueOf(responseModel.getId()))
                .userId(responseModel.getUserId())
                .text(responseModel.getText())
                .createdAt(responseModel.getCreatedAt())
                .build();
          model.add(responseModel.getLinks());
          return model;

    }
}
