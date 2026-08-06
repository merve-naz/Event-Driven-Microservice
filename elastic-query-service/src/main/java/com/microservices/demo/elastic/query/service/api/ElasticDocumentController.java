package com.microservices.demo.elastic.query.service.api;


import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.mapper.DocumentModelMapper;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents") //Spring MVC, MappingJackson2HttpMessageConverter aracılığıyla bu nesneyi Jackson (ObjectMapper) kullanarak JSON'a çeviriyor.
public class ElasticDocumentController {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);
    private final ElasticQueryService elasticQueryService;
    private final DocumentModelMapper documentModelMapper;

    @Value("${server.port}")
    private String port;
    public ElasticDocumentController(ElasticQueryService elasticQueryService, DocumentModelMapper documentModelMapper) {
        this.elasticQueryService = elasticQueryService;
        this.documentModelMapper = documentModelMapper;
    }

    @Operation(summary = "Get all elastic documents.") // Bu anotasyon, endpoint'in ne yaptığını Swagger UI'da gösterir.
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = ElasticQueryServiceResponseModel.class
                                            )
                                    )
                            },
                            description = "Successfully retrieved documents."
                    ),
            }
    )
    @GetMapping("/v1")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocument() {
       List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
        LOG.info("Elasticsearch returned {} of documents on port {}",
                response.size(),
                port);
       return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get  elastic document by id.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = ElasticQueryServiceResponseModel.class
                                            )
                                    )
                            },
                            description = "Successfully retrieved document."),
                    @ApiResponse(
                            responseCode = "404",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = ElasticQueryServiceResponseModel.class
                                            )
                                    )
                            },
                            description = "Document not found.")
            }
    )
    @GetMapping("/v1/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id) { // "Nesnenin içindeki validation kurallarını kontrol et."

        ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get elastic document by id (V2).")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = ElasticQueryServiceResponseModelV2.class
                                            )
                                    )
                            },
                            description = "Successfully retrieved document."),
                    @ApiResponse(
                            responseCode = "404",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = ElasticQueryServiceResponseModelV2.class
                                            )
                                    )
                            },
                            description = "Document not found.")
            }
    )
    @GetMapping("/v2/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByV2Id(@PathVariable @NotEmpty String id) { // "Nesnenin içindeki validation kurallarını kontrol et."

        ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);

        return ResponseEntity.ok(documentModelMapper.toV2Model(response));
    }
   // HTTP standartlarında GET isteklerinin bir Body (gövde) taşıması yasaktır veya önerilmez. Veriler sadece URL'in sonuna eklenerek (?text=merve) gönderilebilir.
    @PostMapping("/v1/get-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(@RequestBody ElasticQueryServiceRequestModel request) {
        System.out.println("TEXT = " + request.getText());
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentsByText(request.getText());
        System.out.println("Tut: " + response+" port: "+port)  ;
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get elastic document by text (V2).")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = ElasticQueryServiceResponseModelV2.class
                                            )
                                    )
                            },
                            description = "Successfully retrieved document."),
                    @ApiResponse(
                            responseCode = "404",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = ElasticQueryServiceResponseModelV2.class
                                            )
                                    )
                            },
                            description = "Document not found.")
            }
    )
    @PostMapping("/v2/get-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponseModelV2>> getDocumentByTextV2(@RequestBody ElasticQueryServiceRequestModel request) {
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentsByText(request.getText());
        List<ElasticQueryServiceResponseModelV2>  response2 = response.stream().map(
                documentModelMapper::toV2Model
        ).toList();
        return ResponseEntity.ok(response2);
    }
//    Controller Java nesnesi döndürür. DispatcherServlet, HTTP cevabını hazırlarken uygun
//    HttpMessageConverter'ı seçer. JSON isteniyorsa MappingJackson2HttpMessageConverter,
//    Jackson (ObjectMapper) kullanarak Java nesnesini JSON'a çevirir ve response body'ye yazar.

}
