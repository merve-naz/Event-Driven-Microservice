package com.microservices.demo.elastic.query.client.service.impl;

import com.microservices.demo.common.util.CollectionUtils;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.client.exception.ElasticQueryClientException;
import com.microservices.demo.elastic.query.client.repository.TwitterElasticSearchQueryRepository;
import com.microservices.demo.elastic.query.client.service.ElasticQueryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Primary
public class TwitterElasticRepositoryQueryClient implements ElasticQueryClient<TwitterIndexModel> {

    private static final Logger Logger = LoggerFactory.getLogger(TwitterElasticQueryClient.class);

    private final TwitterElasticSearchQueryRepository twitterElasticSearchQueryRepository;

    public TwitterElasticRepositoryQueryClient(TwitterElasticSearchQueryRepository twitterElasticSearchQueryRepository) {
        this.twitterElasticSearchQueryRepository = twitterElasticSearchQueryRepository;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Optional<TwitterIndexModel> searchResult = twitterElasticSearchQueryRepository.findById(id);

        TwitterIndexModel result = searchResult.orElseThrow(
                () -> new ElasticQueryClientException(
                        "No document found at elasticsearch with id " + id
                )
        );


        Logger.info(
                "Document with id {} retrieved successfully",
                result.getId()
        );

        return result;

    }

    @Override
    public List<TwitterIndexModel> getIndexModelsByText(String text) {
       List<TwitterIndexModel> searchResults =  twitterElasticSearchQueryRepository.findByText(text);
       System.out.println("!!!! searchResults = " + searchResults);
        Logger.info(
                "{} of documents with text {} retrieved successfully",
                searchResults.size(),
                text
        );
        return searchResults;
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        Iterable<TwitterIndexModel> searchResults =  twitterElasticSearchQueryRepository.findAll();
        List<TwitterIndexModel> searchResult =
                CollectionUtils.getInstance()
                        .getListFromIterable(
                                twitterElasticSearchQueryRepository.findAll()
                        );
        return searchResult;
    }
}
