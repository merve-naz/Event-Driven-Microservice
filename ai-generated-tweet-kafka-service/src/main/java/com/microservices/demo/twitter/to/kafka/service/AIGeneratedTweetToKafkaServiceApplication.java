package com.microservices.demo.twitter.to.kafka.service;


import com.microservices.demo.config.AIGeneratedTweetToKafkaServiceData;
import com.microservices.demo.twitter.to.kafka.service.init.StreamInitializer;
import com.microservices.demo.twitter.to.kafka.service.init.impl.KafkaStreamInitializer;
import com.microservices.demo.twitter.to.kafka.service.runner.AIStreamRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;


import java.time.Duration;
import java.time.temporal.ChronoUnit;

@EnableScheduling
@Slf4j
@ComponentScan(basePackages = "com.microservices.demo")
@SpringBootApplication
public class AIGeneratedTweetToKafkaServiceApplication implements CommandLineRunner {

    private final AIGeneratedTweetToKafkaServiceData configData;
    private final StreamInitializer streamInitializer;
    private final AIStreamRunner aiStreamRunner;
    private final TaskScheduler taskScheduler;

    public AIGeneratedTweetToKafkaServiceApplication(
            AIGeneratedTweetToKafkaServiceData configData,
            StreamInitializer streamInitializer,
            AIStreamRunner aiStreamRunner,
            TaskScheduler taskScheduler,
            KafkaStreamInitializer kafkaStreamInitializer) {
        this.configData = configData;
        this.streamInitializer = streamInitializer;
        this.aiStreamRunner = aiStreamRunner;
        this.taskScheduler = taskScheduler;

    }

    public static void main(String[] args) {

        SpringApplication.run(AIGeneratedTweetToKafkaServiceApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        if (streamInitializer.init()) {
            taskScheduler.scheduleAtFixedRate(aiStreamRunner, Duration.of(
                    configData.getScheduleDurationMs(), ChronoUnit.SECONDS
            ));
        } else {
            throw new RuntimeException("Stream initialization failed");
        }
    }
}