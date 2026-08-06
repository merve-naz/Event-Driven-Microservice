package com.microservices.demo.elastic.query.web.client.common.exception;


public class ElasticQueryWebClientException extends RuntimeException {

    // Parametresiz constructor
    public ElasticQueryWebClientException() {
        super();
    }

    // Sadece hata mesajı gönderildiğinde çalışır
    public ElasticQueryWebClientException(String message) {
        super(message);
    }

    // Hata mesajı ve asıl oluşan exception birlikte gönderildiğinde çalışır
    public ElasticQueryWebClientException(String message, Throwable t) {
        super(message, t);
    }

}
//Throwable, Java'da atılabilen (throw edilebilen) her şeyin en üst sınıfıdır.
//Yani Exception da Throwable'dır, RuntimeException da Throwable'dır.
//Throwable
//    │
//            ├── Error
//    │
//            └── Exception
//          │
//                  ├── RuntimeException   ← Unchecked Exception
//          │
//                  └── Diğer Exception'lar ← Checked Exception