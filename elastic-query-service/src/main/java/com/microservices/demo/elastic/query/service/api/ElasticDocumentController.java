package com.microservices.demo.elastic.query.service.api;


import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import jakarta.validation.constraints.NotEmpty;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class ElasticDocumentController {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);
    private final ElasticQueryService elasticQueryService;

    public ElasticDocumentController(ElasticQueryService elasticQueryService) {
        this.elasticQueryService = elasticQueryService;
    }

    @GetMapping("/")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocument() {

       List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
        LOG.info("Getting document from elastic query service: ",response.size());
       return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id) { // "Nesnenin içindeki validation kurallarını kontrol et."

        ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
        return ResponseEntity.ok(response);
    }
   // HTTP standartlarında GET isteklerinin bir Body (gövde) taşıması yasaktır veya önerilmez. Veriler sadece URL'in sonuna eklenerek (?text=merve) gönderilebilir.
   @PostMapping
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(@RequestBody String text) {
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentsByText(text);
        return ResponseEntity.ok(response);
    }

}
