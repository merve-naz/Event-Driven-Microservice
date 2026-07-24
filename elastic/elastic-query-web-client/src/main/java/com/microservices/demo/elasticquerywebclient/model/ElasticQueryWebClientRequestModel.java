package com.microservices.demo.elasticquerywebclient.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryWebClientRequestModel { // hem arama hem de güncelleme için.

    private String id;

    @NotEmpty
    private String text;
}
