# App Config Data

## Overview

The `app-config-data` module contains shared configuration classes used across the microservices in this project.

Each configuration class is mapped to the corresponding properties defined in the YAML configuration files by using Spring Boot's `@ConfigurationProperties`.

This module also provides the shared Logback configuration used by all services.

---

## Responsibilities

- Centralizes configuration models.
- Maps application properties into Java objects.
- Eliminates duplicated configuration classes across services.
- Provides a shared Logback configuration.

---

## Configuration Classes


| Class                                  | Purpose                                                                                                                                                                                   |
| -------------------------------------- |-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **AIGeneratedTweetToKafkaServiceData** | Stores the configuration of the AI Generated Tweet Kafka Service, including OpenAI settings, prompt template, scheduling interval, and streaming keywords.                                |
| **ElasticConfigData**                  | Stores the common Elasticsearch connection settings (such as the connection URL and index name) used by ElasticsearchConfig, TwitterElasticIndexClient, and TwitterElasticQueryClient.                                                                                                  |
| **ElasticQueryConfigData**             | Stores common Elastic Query configuration, such as the document text field used for searching.                                                                                            |
| **ElasticQueryServiceConfigData**      | Contains Reactive Elastic Query Service settings, including the service version and reactive back-pressure delay configuration.(Reactive Elastic Query Service)                           |
| **ElasticQueryWebClientConfigData**    | Stores the WebClient configuration(Elastic Query Web Client , Reactive Elastic Query Web Client), including timeouts, base URL, endpoints, media types, and service instance information. |
| **KafkaConfigData**                    | Holds common Kafka configuration such as brokers, topic names, partitions, replication factor, and Schema Registry settings.                                                              |
| **KafkaProducerConfigData**            | Stores Kafka producer settings, including serializers, acknowledgements, batching, compression, timeout, and retry configuration.                                                         |
| **KafkaConsumerConfigData**            | Stores Kafka consumer settings, including deserializers, consumer group, polling behavior, concurrency, heartbeat, offset strategy, and Avro deserialization configuration.               |
| **RetryConfigData**                    | Defines retry policy settings such as retry intervals, multiplier, maximum attempts, and sleep duration.                                                                                  |
| **SecurityProperties**                 | Stores security-related application properties, such as ignored endpoint paths.                                                                                                           |
| **UserConfigData**                     | Stores application user credentials and role configuration.                                                                                                                               |


---

## Configuration Flow

```text
application.yml
        │
        ▼
@ConfigurationProperties
        │
        ▼
Configuration Classes
(app-config-data)
        │
        ▼
Spring Bean
        │
        ▼
Injected into Services
```

---

## Shared Logging

The module also contains the shared `logback-common.xml` configuration.

```text
Microservice
(logback.xml)
        │
        ▼
logback-common.xml
(app-config-data)
        │
        ▼
ConsoleAppender
RollingFileAppender
```

Each microservice only defines:

- Application name (`APP_NAME`)
- Log directory (`DEV_HOME`)

while all logging rules are shared through `logback-common.xml`.