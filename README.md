# Event-Driven Microservices with Spring Boot, Kafka and Elasticsearch


---

## 🇹🇷 Projenin Genel Amacı

Bu proje, **Spring Boot**, **Apache Kafka**, **Elasticsearch**, **Spring Cloud** ve **Spring AI** teknolojileri kullanılarak geliştirilmiş **event-driven (olay güdümlü) mikroservis mimarisini** örneklemektedir.

Sistemin temel amacı:

- AI sağlayıcılarından (OpenAI, Ollama vb.) veya farklı veri kaynaklarından tweet üretmek.
- Üretilen tweet olaylarını (event) Apache Kafka üzerinden yayınlamak.
- Aynı event'in birden fazla mikroservis tarafından bağımsız olarak işlenmesini sağlamak.
- Tweet verilerini Elasticsearch'e indekslemek.
- Gerçek zamanlı stream analizleri gerçekleştirmek.
- Analiz sonuçlarını PostgreSQL veritabanında saklamak.
- Kullanıcıların tweetleri web arayüzü üzerinden aramasını sağlamak.

---

## 🇬🇧 Project Overview

This project demonstrates an **event-driven microservice architecture** built using **Spring Boot**, **Apache Kafka**, **Elasticsearch**, **Spring Cloud**, and **Spring AI**.

The main objectives of the project are:

- Generate tweet content from AI providers (OpenAI, Ollama, etc.) or other data sources.
- Publish generated tweet events to Apache Kafka.
- Allow multiple microservices to consume the same events independently.
- Index tweet data into Elasticsearch.
- Perform real-time stream processing and analytics.
- Store analytical results in PostgreSQL.
- Enable users to search tweets through a web interface.

---

## Architecture

```text
                    +----------------------+
                    | AI / Tweet Source    |
                    | OpenAI / Ollama      |
                    +----------+-----------+
                               |
                               v
          +--------------------------------------+
          | AI Generated Tweet Kafka Service      |
          | (Kafka Producer)                      |
          +----------------+----------------------+
                           |
                           v
                  +--------------------+
                  |    Kafka Topic     |
                  +---------+----------+
                            |
             +--------------+--------------+
             |                             |
             v                             v
+---------------------------+   +---------------------------+
| Kafka To Elastic Service  |   | Kafka Streams Service     |
+-------------+-------------+   +-------------+-------------+
              |                               |
              v                               v
     +-------------------+          +----------------------+
     | Elasticsearch     |          | Analytics Service    |
     +---------+---------+          +----------+-----------+
               |                               |
               v                               v
      +-------------------+          +----------------------+
      | Query Service     |          | PostgreSQL           |
      +---------+---------+          +----------------------+
                |
                v
      +-------------------+
      | Web Client        |
      | (Thymeleaf UI)    |
      +-------------------+
```

---

## 🇹🇷 Event Akışı

1. AI sağlayıcısı tweet üretir.
2. AI Generated Tweet Kafka Service tweet event'ini oluşturur.
3. Event Kafka topic'ine yayınlanır.
4. Kafka To Elastic Service event'i tüketerek Elasticsearch'e kaydeder.
5. Kafka Streams Service event üzerinde gerçek zamanlı işlemler gerçekleştirir.
6. Analytics Service analiz sonuçlarını PostgreSQL'e kaydeder.
7. Query Service Elasticsearch'ten verileri sorgular.
8. Web Client sonuçları kullanıcıya gösterir.

---

## 🇬🇧 Event Flow

1. An AI provider generates tweet content.
2. AI Generated Tweet Kafka Service creates a tweet event.
3. The event is published to a Kafka topic.
4. Kafka To Elastic Service consumes the event and indexes it into Elasticsearch.
5. Kafka Streams Service performs real-time stream processing.
6. Analytics Service stores analytical results in PostgreSQL.
7. Query Service retrieves data from Elasticsearch.
8. Web Client displays search results to the user.

---

## Technologies

- Java 21
- Spring Boot
- Spring Cloud
- Spring AI
- Apache Kafka
- Kafka Streams
- Elasticsearch
- PostgreSQL
- Thymeleaf
- Docker