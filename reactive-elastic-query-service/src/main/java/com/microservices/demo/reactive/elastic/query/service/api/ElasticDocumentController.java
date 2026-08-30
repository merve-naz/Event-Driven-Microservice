package com.microservices.demo.reactive.elastic.query.service.api;


import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.reactive.elastic.query.service.business.ElasticQueryService;
import jakarta.validation.Valid;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.net.http.WebSocket;

@RestController
@RequestMapping("/documents")
public class ElasticDocumentController {
    //Reactive'de normal MVC gibi bütün sonuçları bekleyip tek seferde dönmezsin.
    private static final Logger LOG =
            LoggerFactory.getLogger(ElasticDocumentController.class);


    private final ElasticQueryService elasticQueryService;

    public ElasticDocumentController(ElasticQueryService queryService) {
        this.elasticQueryService = queryService;
    }

    @PostMapping(
            value = "/get-doc-by-text",                 // POST /documents/get-doc-by-text
            consumes = MediaType.APPLICATION_JSON_VALUE, // İstek gövdesi JSON olmalı.
            produces = MediaType.TEXT_EVENT_STREAM_VALUE // Cevap Flux olarak stream (SSE) şeklinde gönderilecek.
    )// Spring, Flux'tan gelen verileri SSE formatında istemciye gönderiyor.
    public Flux<ElasticQueryServiceResponseModel> getDocumentByText(
            @RequestBody @Valid ElasticQueryServiceRequestModel requestModel) {

        Flux<ElasticQueryServiceResponseModel> response =
                elasticQueryService.getDocumentsByText(requestModel.getText());

        // Reactive akışı debug amacıyla loglar.
        // (onSubscribe, onNext, onComplete vb.)
        response = response.log();

        LOG.info("Returning from query reactive service for text {}!",
                requestModel.getText());
        return response;
    }
}
//Flux = Veri tipi (Reactive Stream)
//
//Spring WebFlux = Bu veri tiplerini kullanarak web uygulaması geliştiren framework.
//Spring WebFlux ise Spring'in web framework'üdür.
// Görevi:
//HTTP isteğini almak
//Controller'ı çalıştırmak
//Flux veya Mono dönerse bunları HTTP Response'a çevirmek
//Gerekirse SSE olarak stream etmek


//veriyi sürekli veya parça parça iletme yöntemleri (protokoller/teknolojiler)
//Veriyi parça parça göndermek (Streaming)
//                                 │
//   ┌──────────────┬──────────────┬──────────────┬──────────────┐
//   ▼              ▼              ▼              ▼
//SSE         WebSocket    HTTP Chunked     gRPC Streaming

//1. SSE (Server-Sent Events): Sunucu → İstemci
//Sadece sunucu veri gönderir.
//2. WebSocket : İki yönlü iletişim
//Hem client hem server istediği zaman mesaj gönderebilir.
//gRPC Streaming
//Bu da gRPC kullanan uygulamalarda stream yapma yöntemidir.
//SSE, HTTP üzerinde çalışan bir teknolojidir.
//        WebSocket, ayrı bir iletişim protokolüdür, ama bağlantıyı başlatmak için ilk adımda HTTP kullanır.





