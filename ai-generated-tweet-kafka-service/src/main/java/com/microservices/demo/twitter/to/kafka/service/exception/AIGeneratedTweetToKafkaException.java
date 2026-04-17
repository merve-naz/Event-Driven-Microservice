package com.microservices.demo.twitter.to.kafka.service.exception;

public class AIGeneratedTweetToKafkaException extends RuntimeException{

        public AIGeneratedTweetToKafkaException(String message) {
            super(message);
        }

        public AIGeneratedTweetToKafkaException(String message, Throwable cause) {
            super(message, cause);
        }
}
