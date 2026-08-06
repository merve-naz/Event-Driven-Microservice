package com.microservices.demo.reactive.elastic.query.web.client.config;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration // Bu sınıf Spring'in configuration sınıfıdır.
public class WebClientConfig {

    // application.yml'deki webClient ayarlarını tutar.
    private final ElasticQueryWebClientConfigData.WebClient webClientConfig; //WebClient aslında iç (nested) sınıf.

    // ConfigData bean'i Spring tarafından inject edilir.
    public WebClientConfig(ElasticQueryWebClientConfigData clientConfigData) {
        this.webClientConfig = clientConfigData.getWebClient();
    }

    @Bean("webClient") // Spring Container'a WebClient bean'i eklenir.
    WebClient webClient() {

        return WebClient.builder()

                // Tüm isteklerde kullanılacak ortak base URL.
                .baseUrl(webClientConfig.getBaseUrl())

                // Her isteğe varsayılan Content-Type header'ını ekler.
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        webClientConfig.getContentType())

                // Timeout gibi düşük seviyeli TCP ayarlarını kullanan HttpClient bağlanır.
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.from(getTcpClient())
                        ))

                // WebClient nesnesi oluşturulur.
                .build();
    }

    // TCP bağlantısının ayarlarını oluşturur.
    private TcpClient getTcpClient() {

        return TcpClient.create()

                // Sunucuya bağlanmak için maksimum bekleme süresi.
                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        webClientConfig.getConnectTimeoutMs())

                // Bağlantı kurulduktan sonra timeout handler'ları eklenir.
                .doOnConnected(connection ->

                        connection

                                // Sunucudan cevap gelmezse ReadTimeoutException fırlatır.
                                .addHandlerLast(
                                        new ReadTimeoutHandler(
                                                webClientConfig.getReadTimeoutMs(),
                                                TimeUnit.MILLISECONDS))

                                // İstek gönderilemezse WriteTimeoutException fırlatır.
                                .addHandlerLast(
                                        new WriteTimeoutHandler(
                                                webClientConfig.getWriteTimeoutMs(),
                                                TimeUnit.MILLISECONDS))
                );
    }
}