package com.microservices.demo.elastic.query.service.model;

import jakarta.validation.constraints.NotEmpty;

public class ElasticQueryServiceRequestModel {

    private String id;
    @NotEmpty
    private String text;
}
