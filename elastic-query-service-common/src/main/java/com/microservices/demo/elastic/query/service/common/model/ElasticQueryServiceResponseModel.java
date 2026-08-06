package com.microservices.demo.elastic.query.service.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceResponseModel    extends RepresentationModel<ElasticQueryServiceResponseModel> {
    //Asıl amacı link ekleyebilmektir.
    private String id;
    private Long userId;
    private String text;
    private LocalDate  createdAt;
}



//{
//        "id": "1",
//        "userId": 25,
//        "text": "Hello",
//        "createdAt": "2026-06-29T12:00:00",
//        "_links": {
//        "self": {
//        "href": "http://localhost:8080/api/elastic-query/1"
//        }
//        }
//        }
//HATEOAS, response nesnesine link (self, next, update vb.) ekleyebilmek için bu sınıftan
// türetilmesini ister.
// self: "Bu kaynağın kendisi." => Bu kullanıcıya tekrar ulaşmak istersen bu URL'yi kullan.
//update : "Bu kaynağı güncelle."
// next => Bu daha çok sayfalamada (pagination) kullanılır. Sonraki sayfaya gitmek için bu linki kullan.

// HATEOAS kullanan REST API'lerde istemciye "bundan sonra hangi işlemleri yapabilirsin?" bilgisini vermek için kullanılır.