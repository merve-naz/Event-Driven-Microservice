package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "elastic-query-service")
public class ElasticQueryServiceConfigData { //reactive elastıc query servici ıcın.
    private String version;
    private String customAudience;//, Elastic Query Service'in JWT içinde beklediği audience değeridir.
    private Long backPressureDelayMs; //"Her eleman işlendiğinde kaç milisaniye bekleyeyim?"
}
