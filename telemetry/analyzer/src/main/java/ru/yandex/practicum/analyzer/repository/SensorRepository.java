package ru.yandex.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.analyzer.model.Sensor;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, String> {

    List<Sensor> findSensorsByHubId(String hubId);

}
