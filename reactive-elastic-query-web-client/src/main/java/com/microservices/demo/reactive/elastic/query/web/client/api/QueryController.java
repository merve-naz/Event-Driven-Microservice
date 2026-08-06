package com.microservices.demo.reactive.elastic.query.web.client.api;

import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;
import com.microservices.demo.reactive.elastic.query.web.client.service.ElasticQueryWebClient;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.thymeleaf.spring6.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.*;

@Controller
//@Controller olduğu için Spring, dönen String'i normal bir metin olarak değil,
//bir View adı olarak yorumlar. Daha sonra ViewResolver, varsayılan Thymeleaf ayarlarını kullanarak
//classpath:/templates/ klasöründe bu isimde bir .html dosyası arar.
public class QueryController {

    private static final Logger LOG = LoggerFactory.getLogger(QueryController.class);

    private final ElasticQueryWebClient elasticQueryWebClient;

    public QueryController(ElasticQueryWebClient webClient) {
        this.elasticQueryWebClient = webClient;
    }

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("elasticQueryClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @PostMapping(value = "/query-by-text")
    public String queryByText(@Valid ElasticQueryWebClientRequestModel requestModel, Model model) {
        Flux<ElasticQueryWebClientResponseModel> responseModel = elasticQueryWebClient.getDataByText(requestModel);
        responseModel = responseModel.log();
        //Reactor Spring WebFlux'un kullandığı reaktif programlama altyapısıdır; Flux ve Mono onun sınıflarıdır. log() ise bu reaktif veri akışının çalışma adımlarını konsolda görmeni sağlayan bir debug aracıdır.
        IReactiveDataDriverContextVariable reactiveData =
                new ReactiveDataDriverContextVariable(responseModel, 1); //"Bu Flux'tan gelen verileri, geldikçe HTML sayfasına aktar."
        model.addAttribute("elasticQueryClientResponseModels", reactiveData);
        model.addAttribute("searchText", requestModel.getText());
        model.addAttribute("elasticQueryClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        LOG.info("Returning from reactive client controller for text {} !", requestModel.getText());
        return "home";
    }
}

//Reactive
//│
//        ├─ Bir programlama yaklaşımıdır.
//│
//        └── Bu yaklaşımı uygulayan kütüphaneler vardır.
//        │
//                ├── Project Reactor  ← Spring WebFlux bunu kullanır.
//        ├── RxJava
//        └── Akka Streams

//Spring WebFlux → Web uygulamaları geliştirmek için Spring'in reaktif web çatısı.
//Bunun içinde WebClient, reaktif controller'lar,
//router'lar vb. vardır ve bunlar Reactor'ın Mono ve Flux tiplerini kullanır.

//Spring MVC nedir?
// Spring MVC, Spring'in Servlet tabanlı web framework'üdür.
//DispatcherServlet kullanır.
//Genellikle Tomcat üzerinde çalışır.
//Blocking (istek sırasında thread bekler).
//Spring WebFlux nedir?
//Spring WebFlux ise Spring'in Reactive web framework'üdür.
//DispatcherHandler kullanır.
//Reactor (Mono, Flux) kullanır.
//Non-blocking çalışır.
//REST, verinin HTTP üzerinden nasıl sunulacağını tanımlar; Spring MVC ve Spring WebFlux ise bunu gerçekleştiren iki farklı Spring web framework'üdür.


//WebFlux'ta veri sonradan geliyorsa ne olacağı, cevabı nasıl döndürdüğüne bağlıdır:
//Normal REST API (Flux → JSON): Spring HTTP bağlantısını açık tutar ve Flux tamamlandığında cevabı bitirir.
//SSE (TEXT_EVENT_STREAM): Veri geldikçe istemciye anında gönderilir, bağlantı açık kalır.
//Senin Thymeleaf örneğinde: ReactiveDataDriverContextVariable, Flux'tan gelen verileri geldikçe View'e aktarır.