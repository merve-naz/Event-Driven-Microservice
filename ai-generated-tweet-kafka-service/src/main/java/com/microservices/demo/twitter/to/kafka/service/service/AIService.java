package com.microservices.demo.twitter.to.kafka.service.service;

import com.microservices.demo.twitter.to.kafka.service.exception.AIGeneratedTweetToKafkaException;

public interface AIService {

    String generateTweet() throws AIGeneratedTweetToKafkaException;
}
