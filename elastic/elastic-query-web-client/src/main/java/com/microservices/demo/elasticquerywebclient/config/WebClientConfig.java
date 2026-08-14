package com.microservices.demo.elasticquerywebclient.config;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import com.microservices.demo.config.UserConfigData;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.tcp.TcpClient;
import reactor.netty.http.client.HttpClient;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

@Configuration
@LoadBalancerClient(
        name = "elastic-query-service",
        configuration = ElasticQueryServiceInstanceListSupplierConfig.class
)// Spring Cloud LoadBalancer'a "hangi servis için hangi LoadBalancer konfigürasyonunu kullanacağını" söylüyor.

public class WebClientConfig { //Giden isteği yönetir.
    //Bu sınıf, uygulamanın başka servislere göndereceği tüm HTTP isteklerinde kullanılacak ortak WebClient ayarlarını
    // (Base URL, Basic Authentication, Header, Timeout vb.) tek bir yerde yapılandırır.

    // application.yml dosyasındaki WebClient ayarlarını (Base URL, Timeout vb.) tutar.
    private final ElasticQueryWebClientConfigData.WebClient elasticQueryWebClientConfigData;

    // Basic Authentication için kullanıcı adı ve şifre bilgilerini tutar.
    private final UserConfigData userConfigData;

    // Spring gerekli Bean'leri constructor üzerinden otomatik enjekte eder.
    public WebClientConfig(ElasticQueryWebClientConfigData webClientConfigData,
                           UserConfigData userData) {

        this.elasticQueryWebClientConfigData = webClientConfigData.getWebClient();
        this.userConfigData = userData;
    }

    @LoadBalanced
    @Bean("webClientBuilder")
        // Ortak ayarlara sahip bir WebClient.Builder Bean'i oluşturur.
    WebClient.Builder webClientBuilder() {

        return WebClient.builder()

                // Her HTTP isteğine otomatik Basic Authentication ekler.
                .filter(ExchangeFilterFunctions.basicAuthentication(
                        userConfigData.getUsername(),
                        userConfigData.getPassword()))

                // Gönderilecek tüm isteklerde kullanılacak temel adres.//URL'deki host kısmına bakıyor.load balancer
                .baseUrl(elasticQueryWebClientConfigData.getBaseUrl())
                // Varsayılan Content-Type header'ını ekler.
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        elasticQueryWebClientConfigData.getContentType())

                // Varsayılan Accept header'ını ekler.
                .defaultHeader(HttpHeaders.ACCEPT,
                        elasticQueryWebClientConfigData.getAcceptType())

                // Timeout ayarları yapılmış HttpClient'i WebClient'e bağlar.
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.from(getTcpClient()))) //WebFlux daha performanslı çalışmak için Reactor kullanır.

                // WebClient'in veri dönüştürme (Codec) ayarlarını yapılandırır.
                .codecs(clientCodecConfigurer ->

                        clientCodecConfigurer

                                // Varsayılan codec'leri kullanır.
                                .defaultCodecs()

                                // Response'un bellekte tutulabileceği maksimum boyutu belirler.
                                .maxInMemorySize(
                                        elasticQueryWebClientConfigData.getMaxInMemorySize()));
    }
    // TCP bağlantısı için timeout ayarlarını yapan HttpClient'i oluşturur.
    private TcpClient getTcpClient() {

        return TcpClient.create()

                // Sunucuya bağlanırken beklenecek maksimum süre.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        elasticQueryWebClientConfigData.getConnectTimeoutMs())

                // Bağlantı kurulduktan sonra timeout ayarlarını uygular.
                .doOnConnected(connection -> {

                    // Sunucudan cevap gelmezse belirlenen sürede bağlantıyı sonlandırır.
                    connection.addHandlerLast(
                            new ReadTimeoutHandler(
                                    elasticQueryWebClientConfigData.getReadTimeoutMs(),
                                    TimeUnit.MILLISECONDS));

                    // İstek gönderilemezse belirlenen sürede bağlantıyı sonlandırır.
                    connection.addHandlerLast(
                            new WriteTimeoutHandler(
                                    elasticQueryWebClientConfigData.getWriteTimeoutMs(),
                                    TimeUnit.MILLISECONDS));
                });
    }
}
//bAyarlarını yaparsın.
//
//baseUrl
//filter
//header
//codec
//timeout

//Java'nın kendi HttpClient'ı:import java.net.http.HttpClient;
//Java 11 ile geldi.
//Java'nın standart HTTP istemcisidir.
//Spring WebFlux için kullanılmaz.
//2. Reactor Netty HttpClient
//import reactor.netty.http.client.HttpClient;
//Spring WebFlux'un kullandığı HttpClient budur.
//WebClient bunun üzerinde çalışır.
//HttpClient.from() metodu burada vardır.
// WebClient, HTTP isteklerini kendisi TCP ile göndermez. Arkada bunu yapan bir HttpClient kullanır.


//@LoadBalanced'ın gerçekten faydalı olabilmesi için bir Service Discovery
//mekanizmasına veya servis listesini sağlayan bir bileşene ihtiyacı vardır.