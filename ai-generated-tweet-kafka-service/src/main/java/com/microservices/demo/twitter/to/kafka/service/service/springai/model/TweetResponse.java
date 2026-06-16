package com.microservices.demo.twitter.to.kafka.service.service.springai.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

public record TweetResponse(
        String createdAt,
        Long id,
        String text,
        User user

) {
}
