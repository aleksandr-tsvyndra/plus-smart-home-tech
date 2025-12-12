package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Sensor;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    boolean existsByIdAndHubId(String id, String hubId);

    boolean existsAllByIdInAndHubId(List<String> ids, String hubId);

    void deleteByIdAndHubId(String id, String hubId);

}
