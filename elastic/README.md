# elastic-config

The `elastic-config` module is responsible for configuring the Elasticsearch client used by the application.

`ElasticsearchConfig` creates the client configuration using the properties provided by `ElasticConfigData`, such as the connection URL and timeout settings.

During application startup, Spring automatically uses this configuration to create the `ElasticsearchOperations` bean, which is shared by the index and query clients.

### Flow

```text
application.yml
        │
        ▼
ElasticConfigData
        │
        ▼
ElasticsearchConfig
        │
        ▼
ElasticsearchOperations
        │
        ▼
TwitterElasticIndexClient
TwitterElasticQueryClient
```
-------------------

# elastic-index-client

## Purpose

The `elastic-index-client` module is responsible for indexing documents into Elasticsearch.

It provides two different implementations of the `ElasticIndexClient` interface:

- **TwitterElasticIndexClient** – Indexes documents using Spring Data Elasticsearch's `ElasticsearchOperations` API.
- **TwitterElasticRepositoryIndexClient** – Indexes documents using the Spring Data Elasticsearch Repository API.

`ElasticIndexUtil` is used by the operations-based implementation to convert document models into `IndexQuery` objects required by `ElasticsearchOperations.bulkIndex()`.

### Flow

### Operations API

```text
TwitterIndexModel
        │
        ▼
ElasticIndexUtil
        │
        ▼
IndexQuery
        │
        ▼
ElasticsearchOperations
        │
        ▼
Elasticsearch
```

### Repository API

```text
TwitterIndexModel
        │
        ▼
TwitterElasticsearchIndexRepository
        │
        ▼
Elasticsearch
```
--------------------------------

# elastic-model

## Purpose

The `elastic-model` module contains the document models stored in Elasticsearch.

These models define the document structure, field mappings, and metadata used for indexing and querying.

## Components

| Component | Description |
|----------|-------------|
| **IndexModel** | Common interface for all Elasticsearch document models. It defines the `getId()` method so that every document exposes its unique identifier. |
| **TwitterIndexModel** | Represents a Twitter document stored in the `twitter-index` Elasticsearch index. It contains the document fields and Elasticsearch mapping annotations. |

### Model Hierarchy

```text
                 IndexModel
                      ▲
                      │
              TwitterIndexModel
```

### Document Mapping

`TwitterIndexModel` is mapped to the `twitter-index` Elasticsearch index using the `@Document` annotation.

```java
@Document(indexName = "twitter-index")
```

- JPA/Hibernate → ORM (Object Relational Mapping) – Maps Java objects to relational database tables.
- Spring Data Elasticsearch → ODM (Object-to-Document Mapping) – Maps Java objects to Elasticsearch documents instead of relational database tables.

The model contains the following fields:

- **id** – Unique document identifier.
- **userId** – Identifier of the tweet owner.
- **text** – Tweet content.
- **createdAt** – Tweet creation date.
- ----------------------
# elastic-query-client

## Purpose

The `elastic-query-client` module is responsible for querying documents directly from Elasticsearch.

It provides two different implementations of the `ElasticQueryClient` interface:

- **TwitterElasticQueryClient** – Executes search operations using Spring Data Elasticsearch's `ElasticsearchOperations` API.
- **TwitterElasticRepositoryQueryClient** – Executes search operations using the Spring Data Elasticsearch Repository API.

`ElasticQueryUtil` is used by the operations-based implementation to create `Query` objects required by `ElasticsearchOperations`.

### Flow

### Operations API

```text
Search Criteria
        │
        ▼
ElasticQueryUtil
        │
        ▼
Query
        │
        ▼
ElasticsearchOperations
        │
        ▼
Elasticsearch
```

### Repository API

```text
Search Criteria
        │
        ▼
TwitterElasticSearchQueryRepository
        │
        ▼
Elasticsearch
```

| İşlem             | Operations API            | Repository API                            |
| ----------------- | ------------------------- | ----------------------------------------- |
| **Write (Index)** | `bulkIndex()`             | `saveAll()`                               |
| **Read (Query)**  | `searchOne()`, `search()` | `findById()`, `findByText()`, `findAll()` |

--------------------------------

# elastic-query-web-client

## Purpose

The `elastic-query-web-client` module provides a WebClient-based client for querying data through the **Elastic Query Service**.

Instead of connecting directly to Elasticsearch, it sends HTTP requests to the Elastic Query Service, which internally uses the `elastic-query-client` module to execute queries against Elasticsearch.

## Components

| Component | Description |
|----------|-------------|
| **QueryController** | Receives requests from the UI and delegates search operations to the WebClient service. |
| **TwitterElasticQueryWebClient** | Builds and executes HTTP requests using Spring WebClient and maps the responses to Java objects. |
| **WebClientConfig** | Configures the shared `WebClient.Builder` bean, including the base URL, timeouts, and default HTTP settings. |
| **ElasticQueryServiceInstanceListSupplier** | Provides the available instances of the Elastic Query Service for Spring Cloud LoadBalancer. |
| **WebSecurityConfig** | Configures Spring Security settings for the web application. |

### Flow

```text
User
    │
    ▼
QueryController
    │
    ▼
TwitterElasticQueryWebClient
    │
    ▼
Spring WebClient
    │
HTTP Request
    │
    ▼
Elastic Query Service
    │
    ▼
elastic-query-client
    │
    ▼
Elasticsearch
```

Overall, the elastic modules provide a complete integration with Elasticsearch. They cover client configuration, document modeling, indexing, querying, and HTTP-based communication through the Elastic Query Service. Together, these modules encapsulate the Elasticsearch infrastructure behind reusable components, making it easier for other services to interact with Elasticsearch without dealing with low-level implementation details.