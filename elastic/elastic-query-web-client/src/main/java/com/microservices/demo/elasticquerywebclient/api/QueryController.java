package com.microservices.demo.elasticquerywebclient.api;

// @RestController kullanılırsa dönen nesne otomatik JSON'a çevrilip HTTP Response Body'ye yazılır.
// Thymeleaf ile HTML sayfası döndüreceğimiz için @Controller kullanılır.
// =>return edilen String'i view adı olarak kabul eder.
// // Spring, resources/templates/ klasöründe aynı isimli .html dosyasını arar.


import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;
import com.microservices.demo.elasticquerywebclient.service.ElasticQueryWebClient;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.ArrayList;
import java.util.List;

@Controller
public class QueryController {

    private static final Logger LOG =
            LoggerFactory.getLogger(QueryController.class);


    private final ElasticQueryWebClient elasticQueryWebClient;

    public QueryController(ElasticQueryWebClient elasticQueryWebClient) {
        this.elasticQueryWebClient = elasticQueryWebClient;
    }

    // Ana sayfa (index.html)
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Hata sayfası
    @GetMapping("/error")
    public String error() {
        return "error"; // templates/error.html açılır.
    }

    // Home sayfasını açar.
    @GetMapping("/home")
    public String home(Model model) {

        // Thymeleaf'e boş bir RequestModel gönderiyoruz.
        // Böylece form bu nesneye bağlanabilecek.
        model.addAttribute(
                "elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());

        // templates/home.html açılır.
        return "home";
    }

    // Form gönderildiğinde çalışır.
    @PostMapping("/query-by-text")
    public String queryByText(

            // Formdan gelen veriyi RequestModel'e doldurur.
            @Valid ElasticQueryWebClientRequestModel requestModel,

            // Controller'dan Thymeleaf'e veri göndermek için kullanılır.
            Model model) {

        LOG.info("Querying with text {}");

        // Sonuçları tutacak liste oluşturulur.
        List<ElasticQueryWebClientResponseModel> responseModels = elasticQueryWebClient.getDataByText(requestModel);


        // Bulunan sonuçlar Thymeleaf'e gönderilir.
        model.addAttribute(
                "elasticQueryClientResponseModels",
                responseModels);

        // Kullanıcının aradığı kelime tekrar sayfaya gönderilir.
        model.addAttribute(
                "searchText",
                requestModel.getText());

        // Form tekrar boş oluşturulur.Çünkü bu sadece formu temizlemek için.
        model.addAttribute(
                "elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());

        // Aynı home sayfasına dönülür.
        return "home";
    }
}

//MVC Uygulaması
//      │
//Tomcat üzerinde çalışıyor
//      │
//              ├──────────────► Tarayıcıdan istek alıyor
//      │
//              │
//              └──────────────► WebClient ile başka servise istek atıyor
//                          │
//                                  ▼
//Reactor Netty