package com.microservices.demo.common.config;

import com.microservices.demo.config.RetryConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;


@Configuration
public class RetryConfig {

    private final RetryConfigData retryConfigData;


    public RetryConfig(RetryConfigData configData) {
        this.retryConfigData = configData;
    }

    // library’nin default bir instance’ı otomatik gelmiyor.
    //Senin oluşturduğun bean geliyor.
    @Bean
    public RetryTemplate retryTemplate() {

        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();

        exponentialBackOffPolicy.setInitialInterval(
                retryConfigData.getInitialIntervalMs());
        exponentialBackOffPolicy.setMaxInterval(
                retryConfigData.getMaxIntervalMs());
        exponentialBackOffPolicy.setMultiplier(
                retryConfigData.getMultiplier());

        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(
                retryConfigData.getMaxAttempts());

        retryTemplate.setRetryPolicy(simpleRetryPolicy);

//        RetryPolicy → kaç kez denenecek
//        BackOffPolicy → denemeler arasında ne kadar bekleyecek
        return retryTemplate;
    }
}
