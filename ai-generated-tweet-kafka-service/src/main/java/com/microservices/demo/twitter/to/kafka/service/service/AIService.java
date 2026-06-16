package com.microservices.demo.twitter.to.kafka.service.service;

import com.microservices.demo.twitter.to.kafka.service.exception.AIGeneratedTweetToKafkaException;
import com.microservices.demo.twitter.to.kafka.service.service.springai.model.TweetResponse;

public interface AIService {

    TweetResponse generateTweet() throws AIGeneratedTweetToKafkaException;
}
