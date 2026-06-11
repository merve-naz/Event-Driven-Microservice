package com.microservices.demo.elastic.index.client.util;


import com.microservices.demo.elastic.model.index.IndexModel;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ElasticIndexUtil < T extends IndexModel> {
    // burada  amaç,sorgu  nesnelerini oluşturmak için yardımcı bir sınıf sağlamaktır.
    //çünkü elasticsearchOperations.bulkIndex() gibi metodlar IndexQuery nesneleri bekler.
    public List<IndexQuery> getIndexQueries(List<T> elasticIndexModels) {
        return elasticIndexModels.stream()
                .map(document -> new IndexQueryBuilder()
                        .withId(document.getId())
                        .withObject(document)
                        .build())
                .toList();
    }
}
