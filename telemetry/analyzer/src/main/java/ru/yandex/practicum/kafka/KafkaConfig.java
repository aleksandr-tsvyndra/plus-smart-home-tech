package ru.yandex.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Slf4j
@Configuration
public class KafkaConfig {

    @Bean
    @ConfigurationProperties(prefix = "analyzer.kafka.consumer.hub.properties")
    public Properties kafkaHubConsumerProperties() {
        log.info("Готовим конфигурацию для hubConsumer");
        return new Properties();
    }

    @Bean
    @ConfigurationProperties(prefix = "analyzer.kafka.consumer.snapshot.properties")
    public Properties kafkaSnapshotConsumerProperties() {
        log.info("Готовим конфигурацию для snapshotConsumer");
        return new Properties();
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> hubConsumer() {
        log.info("Создаём бин KafkaHubConsumer...");
        return new KafkaConsumer<>(kafkaHubConsumerProperties());
    }

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer() {
        log.info("Создаём бин KafkaSnapshotConsumer...");
        return new KafkaConsumer<>(kafkaSnapshotConsumerProperties());
    }
}
