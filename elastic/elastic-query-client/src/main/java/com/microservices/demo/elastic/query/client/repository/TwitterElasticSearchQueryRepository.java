package com.microservices.demo.elastic.query.client.repository;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TwitterElasticSearchQueryRepository extends ElasticsearchRepository<TwitterIndexModel,String> {

    // Alan bazlı özel sorgu istiyorsan: findBy.AlanAdı. Örn: findByText, findByUserId, findByCreatedAt
    //Spring metod adını okuyup sorguyu kendisi oluşturur.
    List<TwitterIndexModel> findByText(String text);
}

