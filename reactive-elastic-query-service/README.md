# Reactive Elastic Query Service

Bu servis, **Elasticsearch üzerindeki Twitter document'larını reactive olarak sorgulamak ve REST API üzerinden client'a sunmak** için kullanılır.

Normal `elastic-query-service` Spring MVC kullanırken bu servis **Spring WebFlux ve Project Reactor** kullanır. Sonuçlar `List` yerine `Flux` ile reactive olarak işlenir.

## Genel Akış

```text
Client
  ↓
Spring WebFlux
  ↓
ElasticDocumentController
  ↓
TwitterElasticQueryService
  ↓
TwitterReactiveElasticQueryClient
  ↓
ElasticQueryRepository
  ↓
Spring Data Elasticsearch
  ↓
Elasticsearch
```

---

## Spring WebFlux ve Project Reactor

**Spring WebFlux**, Spring'in reactive/non-blocking web framework'üdür.

**Project Reactor** ise reactive programlama altyapısını sağlar:

```text
Mono<T> → 0 veya 1 sonuç
Flux<T> → 0 veya N sonuç
```

Bu projede bir sorgudan birden fazla document gelebileceği için `Flux` kullanılır.

```text
onNext(Document A)
onNext(Document B)
onNext(Document C)
onComplete()
```

Controller `subscribe()` çağırmaz. Controller'ın döndürdüğü `Flux`, Spring WebFlux tarafından HTTP response pipeline'ına bağlanır ve subscription framework tarafından yönetilir.

---

## Controller

`ElasticDocumentController`, HTTP request'ini karşılar ve `ElasticQueryService` metodunu çağırır.

```
@PostMapping(
    value = "/get-doc-by-text",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.TEXT_EVENT_STREAM_VALUE
)
```

- `consumes = application/json` → Request JSON olarak gelir.
- `produces = text/event-stream` → Response SSE stream olarak gönderilir.

`Flux` backend içerisindeki reactive akıştır.

`text/event-stream` ise bu akıştaki elemanların HTTP üzerinden client'a parça parça gönderilmesini sağlar.

```text
Document A → Client
Document B → Client
Document C → Client
```

---

## Service Layer

Projede iki farklı service görevi vardır:

```text
TwitterElasticQueryService
→ Business logic

TwitterReactiveElasticQueryClient
→ Elasticsearch erişimi
```

### TwitterElasticQueryService

`ElasticQueryService` interface'ini implement eder.

```
return reactiveElasticQueryClient
        .getIndexModelByText(text)
        .map(converter::getResponseModel);
```

Elasticsearch'ten gelen:

```text
Flux<TwitterIndexModel>
```

verisini:

```text
Flux<ElasticQueryServiceResponseModel>
```

haline dönüştürür.

---

### TwitterReactiveElasticQueryClient

`ReactiveElasticQueryClient` interface'ini implement eder ve repository üzerinden Elasticsearch verisine erişir.

```
return elasticQueryRepository
        .findByText(text)
        .delayElements(...);
```

`delayElements()` Flux elemanlarının yayınlanması arasına belirlenen süre kadar gecikme ekler.

---

## Neden İki Service Katmanı Var?

Normal projede Elasticsearch erişimi ayrı bir `elastic-query-client` modülünde bulunmaktadır:

```text
elastic-query-service
       ↓
TwitterElasticQueryService
       ↓
elastic-query-client
       ↓
Elasticsearch
```

Reactive projede ayrı bir reactive query client modülü kullanılmadığı için client katmanı doğrudan servisin içine eklenmiştir:

```text
reactive-elastic-query-service
       ↓
TwitterElasticQueryService
       ↓
TwitterReactiveElasticQueryClient
       ↓
ElasticQueryRepository
       ↓
Elasticsearch
```

Bu ayrım WebFlux zorunluluğu değil, **mimari bir tercihtir**.

---

## Repository

```
@Repository
public interface ElasticQueryRepository
        extends ReactiveCrudRepository<TwitterIndexModel, String> {

    Flux<TwitterIndexModel> findByText(String text);
}
```

`ReactiveCrudRepository`, Spring Data'nın reactive CRUD repository interface'idir.

```text
findById() → Mono<T>
findAll()  → Flux<T>
save()     → Mono<T>
delete()   → Mono<Void>
```

`findByText()` ise Spring Data tarafından **derived query** olarak yorumlanır.

```text
findByText("Java")
       ↓
Spring Data Elasticsearch
       ↓
Elasticsearch Query
       ↓
Elasticsearch
```

JPA/Hibernate bu akışta kullanılmaz.

---

## Normal ve Reactive Servis Farkı

| Elastic Query Service | Reactive Elastic Query Service |
|---|---|
| Spring MVC | Spring WebFlux |
| `List<T>` | `Flux<T>` |
| Klasik/blocking yaklaşım | Reactive/non-blocking yaklaşım |
| `ElasticsearchRepository` | `ReactiveCrudRepository` |
| Normal JSON response | SSE streaming kullanılabilir |

---

## Request / Response Akışı

```text
HTTP Request
      ↓
Spring WebFlux
      ↓
ElasticDocumentController
      ↓
TwitterElasticQueryService
      ↓
TwitterReactiveElasticQueryClient
      ↓
ElasticQueryRepository
      ↓
Spring Data Elasticsearch
      ↓
Elasticsearch
      ↓
Flux<TwitterIndexModel>
      ↓
map()
      ↓
Flux<ElasticQueryServiceResponseModel>
      ↓
WebFlux
      ↓
SSE
      ↓
Client
```

---

## Özet

- **Spring WebFlux** → Reactive web framework'üdür.
- **Project Reactor** → `Flux` ve `Mono` reactive tiplerini sağlar.
- **ElasticDocumentController** → HTTP isteklerini karşılar.
- **TwitterElasticQueryService** → Business logic ve model dönüşümünü yapar.
- **TwitterReactiveElasticQueryClient** → Elasticsearch erişimini yönetir.
- **ElasticQueryRepository** → Reactive repository katmanıdır.
- **Spring Data Elasticsearch** → Repository sorgularını Elasticsearch üzerinde çalıştırır.
- **ReactiveCrudRepository** → Reactive CRUD işlemlerini sağlar.
- **TEXT_EVENT_STREAM** → Sonuçların SSE olarak client'a stream edilmesini sağlar.
- -------------------
DIKKAT : İLİŞKİ DURUMLARI

elastic-query-web-client
↓
elastic-query-service
↓
elastic-query-client

REACTIVE:
reactive-elastic-query-web-client
↓
reactive-elastic-query-service