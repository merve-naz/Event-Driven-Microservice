package com.microservices.demo.elastic.query.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.demo")
@EnableElasticsearchRepositories(
        basePackages = "com.microservices.demo.elastic.query.client.repository"
)
public class ElasticQueryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticQueryServiceApplication.class, args);
    }

}