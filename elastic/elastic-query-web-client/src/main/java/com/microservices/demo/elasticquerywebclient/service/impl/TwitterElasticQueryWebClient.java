package com.microservices.demo.elasticquerywebclient.service.impl;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;

import com.microservices.demo.elastic.query.web.client.common.exception.ElasticQueryWebClientException;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;
import com.microservices.demo.elasticquerywebclient.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.util.List;

@Service
public class TwitterElasticQueryWebClient  implements ElasticQueryWebClient {
    private static final Logger LOG =
            LoggerFactory.getLogger(TwitterElasticQueryWebClient.class);

    private final WebClient.Builder webClientBuilder; // İhtiyacın olan Bean'i enjekte et, onu üreten sınıfı değil.

    private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

    public TwitterElasticQueryWebClient(@Qualifier("webClientBuilder") WebClient.Builder webClientBuilder,
                                        ElasticQueryWebClientConfigData elasticQueryWebClientConfigData) {
        this.webClientBuilder = webClientBuilder;
        this.elasticQueryWebClientConfigData = elasticQueryWebClientConfigData;
    }
    private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel) {

        return webClientBuilder

                // Builder'dan gerçek bir WebClient nesnesi oluşturur.
                .build()

                //tek bir HTTP request'in özelliklerini belirler. (HTTP method, endpoint, header, body vb.)
                .method(HttpMethod.valueOf(
                        elasticQueryWebClientConfigData
                                .getQueryByText()
                                .getMethod()))

                // İsteğin gönderileceği endpoint'i belirler. /get-doc-by-text
                .uri(elasticQueryWebClientConfigData
                        .getQueryByText()
                        .getUri())

                // Sunucudan hangi formatta cevap beklediğimizi belirtir.
                // HTTP Header:
                // Accept: application/json
                .accept(MediaType.valueOf(
                        elasticQueryWebClientConfigData
                                .getQueryByText()
                                .getAccept()))

                // HTTP request body'sine gönderilecek veriyi ekler.
                .body(
                        BodyInserters.fromPublisher(

                                // requestModel nesnesini Mono içine koyar.
                                // Mono JSON değildir, sadece requestModel'i taşıyan bir Publisher'dır.
                                Mono.just(requestModel),

                                // Mono içindeki nesnenin tipini Spring'e bildirir.
                                // WebClient bu tip bilgisini kullanarak Jackson ile
                                // requestModel'i JSON'a dönüştürür.
                                createParameterizedTypeReference()
                        )
                )

                // HTTP isteğini gönderir ve ResponseSpec döndürür.(LoadBalancer devreye giriyor.)
                .retrieve()
                .onStatus(

                // Eğer HTTP status kodu 401 ise...
                httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),

                // BadCredentialsException fırlat.
                clientResponse ->
                        Mono.just(new BadCredentialsException("Not authenticated!"))
        )
                .onStatus(

                        // Eğer status kodu herhangi bir 4xx ise...
                        status -> status.is4xxClientError(),


                        // ElasticQueryWebClientException oluştur.
                        clientResponse -> {
                            HttpStatus httpStatus =
                                    HttpStatus.valueOf(clientResponse.statusCode().value());

                            return Mono.just(
                                    new ElasticQueryWebClientException(
                                            httpStatus.getReasonPhrase()));
                        }
                )

                .onStatus(

                        // Eğer status kodu herhangi bir 5xx ise...
                        status -> status.is5xxServerError(),

                        // Genel Exception oluştur.
                        // ElasticQueryWebClientException oluştur.
                        clientResponse -> {
                            HttpStatus httpStatus =
                                    HttpStatus.valueOf(clientResponse.statusCode().value());

                            return Mono.just(
                                    new ElasticQueryWebClientException(
                                            httpStatus.getReasonPhrase()));
                        }
                );
    }
    private <T> ParameterizedTypeReference<T> createParameterizedTypeReference() {
        return new ParameterizedTypeReference<>() {};
    }

    @Override
    public List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
        // Aranan metni log'a yazar.
        LOG.info("Querying by text {}", requestModel.getText());

        return getWebClient(requestModel)//getWebClient(requestModel) başka bir servise HTTP request gönderiyor.

                // HTTP response body'sini okur.
                .bodyToFlux(ElasticQueryWebClientResponseModel.class) //Flux<ResponseModel>

                // Flux'taki tüm elemanları List'e toplar.
                .collectList()//Mono<List<ResponseModel>>

                // Mono'yu bekler ve gerçek List'i döndürür.
                .block();
    }


}


//webClientBuilder
//        .build()
//    .method(...)
//    .uri(...)
//    .accept(...)
//    .body(...)
//    .retrieve()          // ResponseSpec döndürür
//    .onStatus(...)       // ResponseSpec'in metodu
//    .onStatus(...)
//    .onStatus(...);
