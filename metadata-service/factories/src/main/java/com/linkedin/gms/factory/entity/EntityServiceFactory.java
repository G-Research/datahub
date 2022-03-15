package com.linkedin.gms.factory.entity;

import com.linkedin.gms.factory.common.TopicConventionFactory;
import com.linkedin.metadata.dao.producer.EntityKafkaMetadataEventProducer;
import com.linkedin.metadata.entity.EntityService;
import com.linkedin.metadata.entity.cassandra.CassandraAspectDao;
import com.linkedin.metadata.entity.cassandra.CassandraEntityService;
import com.linkedin.metadata.entity.ebean.EbeanAspectDao;
import com.linkedin.metadata.entity.ebean.EbeanEntityService;
import com.linkedin.metadata.models.registry.EntityRegistry;
import com.linkedin.mxe.TopicConvention;
import org.apache.avro.generic.IndexedRecord;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Nonnull;
 

@Configuration
public class EntityServiceFactory {

  @Bean(name = "entityService")
  @DependsOn({"cassandraAspectDao", "kafkaEventProducer", TopicConventionFactory.TOPIC_CONVENTION_BEAN, "entityRegistry"})
  @ConditionalOnProperty(name = "ENTITY_SERVICE_IMPL", havingValue = "cassandra")
  @Nonnull
  protected EntityService createInstance(
      Producer<String, ? extends IndexedRecord> producer,
      TopicConvention convention,
      CassandraAspectDao aspectDao,
      EntityRegistry entityRegistry) {

    final EntityKafkaMetadataEventProducer metadataProducer = new EntityKafkaMetadataEventProducer(producer, convention);
    return new CassandraEntityService(aspectDao, metadataProducer, entityRegistry);
  }

  @Bean(name = "entityService")
  @DependsOn({"ebeanAspectDao", "kafkaEventProducer", TopicConventionFactory.TOPIC_CONVENTION_BEAN, "entityRegistry"})
  @ConditionalOnProperty(name = "ENTITY_SERVICE_IMPL", havingValue = "ebean", matchIfMissing = true)
  @Nonnull
  protected EntityService createInstance(
      Producer<String, ? extends IndexedRecord> producer,
      TopicConvention convention,
      EbeanAspectDao aspectDao,
      EntityRegistry entityRegistry) {

    final EntityKafkaMetadataEventProducer metadataProducer = new EntityKafkaMetadataEventProducer(producer, convention);
    return new EbeanEntityService(aspectDao, metadataProducer, entityRegistry);
  }
}
