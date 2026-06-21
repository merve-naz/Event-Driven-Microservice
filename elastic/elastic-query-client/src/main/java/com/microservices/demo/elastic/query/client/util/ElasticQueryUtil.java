package com.microservices.demo.elastic.query.client.util;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Component;

@Component
public class ElasticQueryUtil  {


//    {
//        "query": {
//        "ids": {
//            "values": [
//            "gönderdiğin_id_değeri"
//      ]
//        }
//    }
//    }
    //withIds kullanılarak doğrudan doküman ID'leri üzerinden bir arama yapılmış. Yeni
    //Elasticsearch'te her verinin (dokümanın) bir Sistem ID'si (_id) vardır, bir de senin nesnenin içine kendi koyduğun Normal ID alanı (id) vardır.
    public NativeQuery getSearchQueryById(String id) {
         return NativeQuery.builder()
                .withQuery(q -> q.ids(i -> i.values(id)))
                .build();
    }

//    {
//        "query": {
//        "bool": {
//            "must": [
//            "match": {
//  "text": {
//    "query": "Artificial Intelligence",
//    "fuzziness": "AUTO"
//  }
//}
//            {
//                "term": {
//                "status": "ACTIVE"
//            }
//            }
//      ]
//        }
//    }
//    }
    public NativeQuery getSearchQueryByFieldText(String field, String text) {

        return NativeQuery.builder().
                withQuery(
                        q -> q.bool(b -> b.must(m -> m.match(
                                                t -> t.field(field).query(text)
                                        )
                                )
                        )
                ).build();
    }

    // matchAll sorgusunda: Filtreleme yoktur, arama yoktur. Amacınız indeksteki (tablodaki) her şeyi koşulsuz şartsız listelemektir. D
    public NativeQuery getSearchQueryForAll() {
        return NativeQuery.builder().
                withQuery(
                        q -> q.matchAll(
                                ma ->ma
                        )
                ).build();
    }

}





