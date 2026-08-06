package com.microservices.demo.elastic.query.service.model.assembler;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.service.api.ElasticDocumentController;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.common.transformer.ElasticToResponseModelTransformer;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ElasticQueryServiceResponseModelAssembler  extends
        RepresentationModelAssemblerSupport <
                TwitterIndexModel,
                ElasticQueryServiceResponseModel>
{ // dönüştürücü

    private final ElasticToResponseModelTransformer elasticToResponseModelTransformer;

    public ElasticQueryServiceResponseModelAssembler(ElasticToResponseModelTransformer elasticToResponseModelTransformer) {
        super(TwitterIndexModel.class, ElasticQueryServiceResponseModel.class);
        this.elasticToResponseModelTransformer = elasticToResponseModelTransformer;
    }

    @Override
    public ElasticQueryServiceResponseModel toModel(TwitterIndexModel twitterIndexModel) {
        ElasticQueryServiceResponseModel responseModel =
                elasticToResponseModelTransformer.getResponseModel(twitterIndexModel);
        //WebMvcLinkBuilder, Controller'daki endpointlerden otomatik olarak HATEOAS linkleri (URL) üretir.
        responseModel.add(
                linkTo(
                        methodOn(ElasticDocumentController.class)
                                .getDocumentById(twitterIndexModel.getId())
                ).withSelfRel()
        );
//        WebMvcLinkBuilder → Link (URL) oluşturan sınıf.
//        methodOn() → Hangi controller metoduna link verileceğini seçer.
//        linkTo() → O metodun gerçek URL'sini oluşturur.
//        withSelfRel() / withRel() → Oluşturulan linke bir isim (self, documents, update vb.) verir.

        responseModel.add(
                linkTo(ElasticDocumentController.class)
                        .withRel("documents"));
        return responseModel;
    }
    public List<ElasticQueryServiceResponseModel> toModels(
            List<TwitterIndexModel> twitterIndexModels) {

   return twitterIndexModels.stream().map(
           i -> toModel(i)
   ).toList();
    }
}
