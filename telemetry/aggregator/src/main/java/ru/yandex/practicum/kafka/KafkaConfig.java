package ru.yandex.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Slf4j
@Configuration
public class KafkaConfig {

    @Bean
    @ConfigurationProperties(prefix = "aggregator.kafka.producer.properties")
    public Properties kafkaProducerProperties() {
        log.info("Готовим конфигурацию для Kafka-продюсера");
        return new Properties();
    }

    @Bean
    @ConfigurationProperties(prefix = "aggregator.kafka.consumer.properties")
    public Properties kafkaConsumerProperties() {
        log.info("Готовим конфигурацию для Kafka-консьюмера");
        return new Properties();
    }

    @Bean
    public KafkaProducer<String, SpecificRecordBase> kafkaProducer() {
        log.info("Создаём бин KafkaProducer...");
        return new KafkaProducer<>(kafkaProducerProperties());
    }

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> kafkaConsumer() {
        log.info("Создаём бин KafkaConsumer...");
        return new KafkaConsumer<>(kafkaConsumerProperties());
    }
}
