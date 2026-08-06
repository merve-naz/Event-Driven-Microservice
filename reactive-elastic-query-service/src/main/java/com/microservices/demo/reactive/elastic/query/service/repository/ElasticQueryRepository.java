package com.microservices.demo.reactive.elastic.query.service.repository;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import org.apache.http.protocol.HTTP;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import javax.swing.*;

//JPA → JDBC ile SQL gönderiyor.
//Elasticsearch → HTTP ile JSON (Query DSL) gönderiyor.

@Repository
public  interface  ElasticQueryRepository extends ReactiveCrudRepository<TwitterIndexModel, String>{
    Flux<TwitterIndexModel> findByText(String text);
}


//Spring Data Elasticsearch ->

//Bunları kendin yazmadan kullanabilirsin.
//save(...)
//findById(...)
//findAll()
//deleteById(...)
//delete(...)
//existsById(...)
//count()

//| Repository             | Dönüş tipi                 |
//        | ---------------------- | -------------------------- |
//        | CrudRepository         | `User`, `Iterable<User>`   |
//        | JpaRepository          | `User`, `List<User>`       |
//        | ReactiveCrudRepository | `Mono<User>`, `Flux<User>` |
//
//CrudRepository
//       ▲
//               │
//PagingAndSortingRepository
//       ▲
//               │
//JpaRepository




//| Katman              | Görevi                                                                          |
//        | ------------------- | ------------------------------------------------------------------------------- |
//        | **
//Spring Data JPA** | `findByEmail`, `findByNameAndAge` gibi metod isimlerini okuyup sorgu oluşturur. |
//        | **Hibernate**       | Bu sorguyu SQL'e çevirir ve çalıştırır.                                         |
//        | **JDBC**            | SQL'i veritabanına gönderir.                                                    |
//
//Spring Data JPA
//      ↓
//Hibernate
//      ↓
//JDBC
//      ↓
//Database


//findByText()
//      │
//              ▼
//Spring Data Elasticsearch
//      │
//              ▼
//Elasticsearch Query DSL (JSON)
//      │
//              ▼
//Elasticsearch Java Client
//        (veya Reactive Elasticsearch Client)
//      │
//              ▼
//HTTP
//      │
//              ▼
//Elasticsearch

//Spring Data JPA → Hibernate + JDBC
//Spring Data Elasticsearch → Elasticsearch Java Client + HTTP