package com.microservices.demo.twitter.to.kafka.service.runner;

import com.microservices.demo.twitter.to.kafka.service.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AIStreamRunner implements  Runnable{

    private  final AIService aiService;

    public AIStreamRunner(AIService aiService) {
        this.aiService = aiService;
    }

    @Override
 public void run (){
        String  generatedTweet = aiService.generateTweet();
       log.info("Generated Tweet: {}", generatedTweet);
    }
}
