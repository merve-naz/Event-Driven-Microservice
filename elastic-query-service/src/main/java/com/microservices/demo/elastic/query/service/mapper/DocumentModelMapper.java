package com.microservices.demo.elastic.query.service.mapper;

import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV3;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

@Component
public class DocumentModelMapper {

    public ElasticQueryServiceResponseModelV3 toV3Model (ElasticQueryServiceResponseModel responseModel) {
        ElasticQueryServiceResponseModelV3 model =  ElasticQueryServiceResponseModelV3.builder()
                .id(Long.valueOf(responseModel.getId()))
                .userId(responseModel.getUserId())
                .text(responseModel.getText())
                .text2("Versiyon 3 text")
                .build();
          model.add(responseModel.getLinks());
          return model;

    }

}
