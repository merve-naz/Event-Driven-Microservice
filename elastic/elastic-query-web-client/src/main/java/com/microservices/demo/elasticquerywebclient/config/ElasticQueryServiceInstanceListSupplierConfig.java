package com.microservices.demo.elasticquerywebclient.config;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ElasticQueryServiceInstanceListSupplierConfig
        implements ServiceInstanceListSupplier {

    // Spring, ElasticQueryWebClientConfigData bean'ini enjekte eder.
    private final ElasticQueryWebClientConfigData configData;

    public ElasticQueryServiceInstanceListSupplierConfig(
            ElasticQueryWebClientConfigData configData) {
        this.configData = configData;
    }

    @Override
    public String getServiceId() {
        // Config'deki servis adını döndürür.
        return configData.getWebClient().getServiceId();
    }

    // Spring Cloud LoadBalancer bu metodu otomatik çağırır.
    @Override
    public Flux<List<ServiceInstance>> get() {

        return Flux.just(
                configData.getWebClient()
                        .getInstances()
                        .stream()
                        .map(instance ->
                                new DefaultServiceInstance(
                                        instance.getId(),      // Instance Id
                                        getServiceId(),        // Servis adı
                                        instance.getHost(),    // Host
                                        instance.getPort(),    // Port
                                        false                  // HTTPS kullanılmıyor
                                ))
                        .collect(Collectors.toList())
        );
    }
}