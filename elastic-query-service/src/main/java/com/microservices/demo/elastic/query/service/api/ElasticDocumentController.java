package com.microservices.demo.elastic.query.service.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.mapper.DocumentModelMapper;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import jakarta.validation.constraints.NotEmpty;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documents") //Spring MVC, MappingJackson2HttpMessageConverter aracılığıyla bu nesneyi Jackson (ObjectMapper) kullanarak JSON'a çeviriyor.
public class ElasticDocumentController {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);
    private final ElasticQueryService elasticQueryService;
    private final DocumentModelMapper documentModelMapper;

    public ElasticDocumentController(ElasticQueryService elasticQueryService, DocumentModelMapper documentModelMapper) {
        this.elasticQueryService = elasticQueryService;
        this.documentModelMapper = documentModelMapper;
    }

    @GetMapping("/v1")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocument() {
       List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
        LOG.info("Getting document from elastic query service: ",response.size());
       return ResponseEntity.ok(response);
    }


    @GetMapping("/v1/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id) { // "Nesnenin içindeki validation kurallarını kontrol et."

        ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/v2/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByV2Id(@PathVariable @NotEmpty String id) { // "Nesnenin içindeki validation kurallarını kontrol et."

        ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);

        return ResponseEntity.ok(documentModelMapper.toV2Model(response));
    }
   // HTTP standartlarında GET isteklerinin bir Body (gövde) taşıması yasaktır veya önerilmez. Veriler sadece URL'in sonuna eklenerek (?text=merve) gönderilebilir.
   @PostMapping("/v1/get-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(@RequestBody String text) {
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentsByText(text);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/v2/get-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponseModelV2>> getDocumentByTextV2(@RequestBody String text) {
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentsByText(text);
        List<ElasticQueryServiceResponseModelV2>  response2 = response.stream().map(
                documentModelMapper::toV2Model
        ).toList();
        return ResponseEntity.ok(response2);
    }
//    Controller Java nesnesi döndürür. DispatcherServlet, HTTP cevabını hazırlarken uygun
//    HttpMessageConverter'ı seçer. JSON isteniyorsa MappingJackson2HttpMessageConverter,
//    Jackson (ObjectMapper) kullanarak Java nesnesini JSON'a çevirir ve response body'ye yazar.

}
