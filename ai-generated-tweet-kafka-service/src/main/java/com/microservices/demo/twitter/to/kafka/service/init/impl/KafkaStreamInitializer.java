package com.microservices.demo.twitter.to.kafka.service.init.impl;

import com.microservices.demo.twitter.to.kafka.service.init.StreamInitializer;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamInitializer implements StreamInitializer {
    @Override
    public boolean init() {
        return true;
    }
}