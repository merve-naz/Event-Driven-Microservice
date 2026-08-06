package com.microservices.demo.elastic.query.service.model;

import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceResponseModelV2  extends RepresentationModel<ElasticQueryServiceResponseModel> {
    private Long id;
    private Long userId;
    private String text;
    private LocalDate createdAt;
}
