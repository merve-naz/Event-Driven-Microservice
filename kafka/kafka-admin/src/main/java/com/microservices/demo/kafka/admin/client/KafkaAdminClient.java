package com.microservices.demo.kafka.admin.client;
import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.RetryConfigData;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.KafkaException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaAdminClient {
    private final AdminClient adminClient;
    private final KafkaConfigData kafkaConfigData;
    private final RetryConfigData retryConfigData;
    private final RetryTemplate retryTemplate;
    private final WebClient webClient;


    public KafkaAdminClient(AdminClient adminClient, KafkaConfigData kafkaConfigData, RetryConfigData retryConfigData, RetryTemplate retryTemplate, WebClient webClient) {
        this.adminClient = adminClient;
        this.kafkaConfigData = kafkaConfigData;
        this.retryConfigData = retryConfigData;
        this.retryTemplate = retryTemplate;
        this.webClient = webClient;
    }


    public void checkSchemaRegistry() {
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        while (getSchemaRegistryStatus() != HttpStatus.OK) {
            checkMaxRetry(maxRetry--, retryConfigData.getMaxAttempts());
            sleep(sleepTimeMs);
            sleepTimeMs *= multiplier;
        }

    }

    private HttpStatus getSchemaRegistryStatus() {
        try {
            return (HttpStatus) webClient
                    .method(HttpMethod.GET)
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    // request gönderiliyor ve response alınıyor
                    .exchange()
                    // gelen response içinden sadece HTTP status code alınıy
                    .map(clientResponse -> clientResponse.statusCode())
                    // reactive Mono sonucunu normal senkron değere çevirip bekliyor
                    .block();
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }

    public void createTopics() {
        CreateTopicsResult createTopicResult;
        try {
            createTopicResult = retryTemplate.execute(this::doCreateTopic);
        } catch (Throwable t) {
            throw new KafkaException("Reaching max number of retry attempts for creating kafka topics. " + "Topics could not be created.", t);
        }
        checkTopicsCreated(); // Kafka request kabul etti ,AMA topic gerçekten oluştu mu?
    }

    // CreateTopicsResult bir kafka admin client sınıfı , topic oluşturma işleminin sonucunu tutar.
    private CreateTopicsResult doCreateTopic(RetryContext retryContext) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
        List<NewTopic> kafkaTopics = topicNames.stream().map(topic -> new NewTopic(topic.trim(), kafkaConfigData.getNumPartitions(), kafkaConfigData.getReplicationFactor())).toList();

        return adminClient.createTopics(kafkaTopics);
    }

    //  Custom retry logic (asıl olay burada)
    public void checkTopicsCreated() {
        Collection<TopicListing> topics = getTopics();

        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();

        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topic)) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    private void sleep(Long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new KafkaException("error while slleep");
        }
    }

    private Collection<TopicListing> getTopics() {
        try {
            return retryTemplate.execute(this::doGetTopics);
        } catch (Throwable t) {
            throw new KafkaException("Reaching max number of retry attempts for getting kafka topics. " + "Topics could not be retrieved.", t);
        }
    }

    // TopicListing bir kafka admin client sınıfı , Kafka cluster'ında bulunan topic'lerin bilgilerini tutar.
    private Collection<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {

        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        if (topics != null) {
            topics.forEach(topic -> System.out.println("Topic name: " + topic.name() + " Topic id: " + topic.topicId() + " Is internal: " + topic.isInternal()));
        }
        return topics;
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
        if (topics == null) {
            return false;
        }
        return topics.stream().anyMatch(topic -> topic.name().equals(topicName));

    }

    private void checkMaxRetry(int retry, Integer maxRetry) {
        if (retry > maxRetry) {
            throw new KafkaException("Reached max number of retry for reading kafka topic(s)!");
        }
    }


}
