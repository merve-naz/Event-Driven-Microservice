package com.microservices.demo.elastic.model.index;

/**
 * Elasticsearch'e gönderilecek tüm modellerin uygulaması gereken ortak interface.
 */
public interface IndexModel {

    /**
     * Dokümanın benzersiz id'sini döndürür.
     */
    String getId();
}