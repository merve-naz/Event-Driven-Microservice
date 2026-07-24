package com.microservices.demo.elastic.query.service.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ElasticQueryServiceRequestModel {

    private String id;
    @NotEmpty
    private String text;
}
