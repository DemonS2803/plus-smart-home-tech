package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {

    @Id
    private String id;
    private String hubId;

    @Override
    public String toString() {
        return "Sensor{" +
                "id='" + id + '\'' +
                '}';
    }
}
