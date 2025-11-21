package ru.yandex.practicum.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "collector.kafka.producer.topics")
public class KafkaTopicNames {
    private String sensorsTopic;
    private String hubsTopic;
}
