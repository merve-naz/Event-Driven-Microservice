# Reactive Elastic Query Web Client

`reactive-elastic-query-web-client`, kullanıcıya HTML arayüzü sunan ve **HTTP üzerinden `reactive-elastic-query-service` servisini çağıran reactive web uygulamasıdır**.

Bu uygulama Elasticsearch'e doğrudan bağlanmaz. Kullanıcıdan arama bilgisini alır, Query Service'e HTTP request gönderir ve gelen reactive sonuçları **Thymeleaf ile HTML sayfasında gösterir**.

---

## Genel Akış

```text
Browser / User
      ↓
QueryController
      ↓
TwitterElasticQueryWebClient
      ↓
Spring WebClient
      ↓ HTTP
reactive-elastic-query-service
      ↓
Elasticsearch
      ↓
Flux<ResponseModel>
      ↓
ReactiveDataDriverContextVariable
      ↓
Thymeleaf
      ↓
HTML / Browser
```

Temel olarak:

```text
Reactive Elastic Query Web Client
→ UI oluşturur ve Query Service'e HTTP isteği gönderir.

Reactive Elastic Query Service
→ İsteği karşılar ve Elasticsearch sorgusunu gerçekleştirir.
```

---

## Spring WebFlux ve Project Reactor

Uygulama **Spring WebFlux** kullanır.

Spring WebFlux, Spring'in reactive/non-blocking web framework'üdür. Reactive programlama altyapısı olarak **Project Reactor** kullanır.

```text
Mono<T> → 0 veya 1 eleman
Flux<T> → 0 veya N eleman
```

Bu projede sorgudan birden fazla document gelebileceği için sonuçlar `Flux` olarak işlenir.

Spring `WebClient` da reactive bir HTTP client'tır. WebClient kullanmak uygulamanın mutlaka WebFlux olması gerektiği anlamına gelmez; Spring MVC içerisinde de kullanılabilir. Ancak bu projede WebFlux, WebClient ve Flux/Mono birlikte kullanılarak reactive bir yapı oluşturulmuştur.

---

## QueryController ve Thymeleaf

`QueryController`, browser'dan gelen HTTP isteklerini karşılar.

```java
@Controller
public class QueryController
```

Burada `@RestController` yerine `@Controller` kullanılır çünkü uygulama JSON response döndürmek yerine **Thymeleaf ile HTML sayfası oluşturur**.

Örneğin:

```java
@GetMapping("/home")
public String home(Model model) {
    ...
    return "home";
}
```

Buradaki:

```java
return "home";
```

bir response body değil, **View adıdır**.

Thymeleaf:

```text
templates/home.html
```

dosyasını bulur ve HTML olarak render eder.

Akış:

```text
QueryController
      ↓
Spring Model
      ↓
Thymeleaf
      ↓
home.html
      ↓
Browser
```

---

## Text ile Sorgulama

Kullanıcı bir text aradığında:

```java
@PostMapping("/query-by-text")
```

çalışır.

Controller:

```java
Flux<ElasticQueryWebClientResponseModel> responseModel =
        elasticQueryWebClient.getDataByText(requestModel);
```

ile Query Service'e yapılacak HTTP çağrısını başlatır.

Controller Elasticsearch'e doğrudan erişmez:

```text
QueryController
      ↓
ElasticQueryWebClient
      ↓
TwitterElasticQueryWebClient
      ↓
Spring WebClient
      ↓ HTTP
reactive-elastic-query-service
```

---

## ReactiveDataDriverContextVariable

Query Service'ten gelen sonuç:

```java
Flux<ElasticQueryWebClientResponseModel>
```

şeklindedir.

Reactive `Flux` verisini Thymeleaf'e aktarabilmek için:

```java
IReactiveDataDriverContextVariable reactiveData =
        new ReactiveDataDriverContextVariable(responseModel, 1);
```

kullanılır.

`ReactiveDataDriverContextVariable`, **Flux ile Thymeleaf arasında bağlantı kurar**.

```text
Flux<ResponseModel>
      ↓
ReactiveDataDriverContextVariable
      ↓
Spring Model
      ↓
Thymeleaf
      ↓
HTML
```

Buradaki `1`, verilerin Thymeleaf tarafından **1 elemanlık buffer gruplarıyla** işlenmesini sağlar.

Böylece bütün Flux'u önce `List` haline getirip beklemek yerine reactive veri template rendering sürecinde kullanılabilir.

---

## ElasticQueryWebClient

`ElasticQueryWebClient`, Query Service'e yapılacak HTTP çağrısının interface'idir.

```text
ElasticQueryWebClient
        ↑
   implements
        │
TwitterElasticQueryWebClient
```

Controller:

```java
private final ElasticQueryWebClient elasticQueryWebClient;
```

ile doğrudan implementation'a bağımlı olmaz.

---

## TwitterElasticQueryWebClient

`TwitterElasticQueryWebClient`, Spring'in **WebClient** sınıfını kullanarak `reactive-elastic-query-service` servisine HTTP request gönderir.

```java
return getWebClient(requestModel)
        .bodyToFlux(ElasticQueryWebClientResponseModel.class);
```

Buradaki:

```java
bodyToFlux(...)
```

HTTP response body'sini reactive olarak:

```text
Flux<ElasticQueryWebClientResponseModel>
```

şeklinde okur.

Akış:

```text
TwitterElasticQueryWebClient
      ↓
Spring WebClient
      ↓ HTTP Request
Reactive Query Service
      ↓
HTTP Response
      ↓
bodyToFlux()
      ↓
Flux<ResponseModel>
```

---

## Spring WebClient

Spring `WebClient`, başka bir servise **reactive/non-blocking HTTP request göndermek** için kullanılan HTTP client'tır.

Burada iki kavram karıştırılmamalıdır:

```text
reactive-elastic-query-web-client
→ Uygulamanın/modülün adı.

Spring WebClient
→ Başka servise HTTP isteği gönderen Spring class'ı.
```

Request şu şekilde hazırlanır:

```java
webClient
    .method(...)
    .uri(...)
    .accept(...)
    .body(...)
    .retrieve();
```

- `method()` → HTTP metodunu belirler.
- `uri()` → Çağrılacak endpoint'i belirler.
- `accept()` → Beklenen response tipini belirler.
- `body()` → Request modelini request body'ye ekler.
- `retrieve()` → HTTP response'u işlemek için reactive response zincirini oluşturur.

---

## Error Handling

HTTP hataları `onStatus()` ile kontrol edilir:

```text
401 Unauthorized
→ BadCredentialsException

4xx Client Error
→ ElasticQueryWebClientException

5xx Server Error
→ ElasticQueryWebClientException
```

Böylece Query Service'ten gelen HTTP hataları reactive akış içerisinde hata olarak işlenir.

---

## WebClientConfig

`WebClientConfig`, Query Service'e HTTP isteği gönderecek Spring `WebClient` bean'ini oluşturur.

```java
@Bean("webClient")
WebClient webClient()
```

Temel ayarlar `application.yml` üzerinden alınır:

```text
baseUrl
contentType
connectTimeout
readTimeout
writeTimeout
```

Timeout'lar:

```text
Connect Timeout → Sunucuya bağlantı kurma süresi
Read Timeout    → Response bekleme süresi
Write Timeout   → Request gönderme süresi
```

---

## Security

Reactive WebFlux security yapılandırmasında:

```java
SecurityWebFilterChain
```

kullanılır.

Mevcut configuration:

```java
.authorizeExchange(exchange ->
        exchange.anyExchange().permitAll())
.csrf(ServerHttpSecurity.CsrfSpec::disable)
```

Bu nedenle:

```text
Tüm endpoint'ler → permitAll
CSRF             → disabled
```

olarak yapılandırılmıştır.

---

## Request / Response Akışı

Kullanıcı bir text aradığında tam akış:

```text
Browser
   ↓
POST /query-by-text
   ↓
QueryController
   ↓
ElasticQueryWebClient
   ↓
TwitterElasticQueryWebClient
   ↓
Spring WebClient
   ↓ HTTP
reactive-elastic-query-service
   ↓
Elasticsearch
   ↓
HTTP Response
   ↓
bodyToFlux()
   ↓
Flux<ResponseModel>
   ↓
ReactiveDataDriverContextVariable
   ↓
Thymeleaf
   ↓
home.html
   ↓
Browser
```

---

## Özet

- **QueryController** → Browser'dan gelen istekleri karşılar.
- **Thymeleaf** → Server-side HTML sayfasını oluşturur.
- **ReactiveDataDriverContextVariable** → `Flux` verisini Thymeleaf'e aktarır.
- **ElasticQueryWebClient** → Query Service çağrısının interface'idir.
- **TwitterElasticQueryWebClient** → HTTP çağrısını gerçekleştirir.
- **Spring WebClient** → Query Service'e reactive HTTP request gönderir.
- **bodyToFlux()** → HTTP response'u `Flux` olarak okur.
- **WebClientConfig** → WebClient ve timeout ayarlarını yapılandırır.
- **SecurityWebFilterChain** → Reactive security yapılandırmasını yönetir.
- **Project Reactor** → `Flux` ve `Mono` reactive tiplerini sağlar.

```text
Browser
   ↓
Reactive Web Client
   ↓
Spring WebClient
   ↓ HTTP
Reactive Query Service
   ↓
Elasticsearch
   ↓
Flux
   ↓
Thymeleaf
   ↓
HTML
```