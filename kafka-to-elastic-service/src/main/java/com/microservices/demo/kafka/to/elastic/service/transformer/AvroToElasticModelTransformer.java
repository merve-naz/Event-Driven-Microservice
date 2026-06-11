package com.microservices.demo.kafka.to.elastic.service.transformer;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

// Kafka'dan gelen Avro modelini, Elasticsearch'e kaydedilecek Index modeline çevirmek.
@Component
public class AvroToElasticModelTransformer {
   public List<TwitterIndexModel> getElasticModels(List<TwitterAvroModel> avroModels) {
       return avroModels.stream().map(
               avro -> TwitterIndexModel.builder()
                       .id(String.valueOf(avro.getId()))
                       .userId(avro.getUserId())
                       .text(avro.getText())
                       .createdAt(
                       Instant.ofEpochMilli(avro.getCreatedAt())
                               .atZone(ZoneId.systemDefault())
                               .toLocalDateTime()
               )
                       .build()
       ).toList();
   }
}
