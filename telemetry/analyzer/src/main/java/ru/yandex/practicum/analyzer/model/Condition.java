package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

@Entity
@Table(name = "conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ConditionTypeAvro type;
    @Enumerated(EnumType.STRING)
    private ConditionOperationAvro operation;
    private Integer value;

    @Override
    public String toString() {
        return "Condition{" +
                "id=" + id +
                ", type=" + type +
                ", operation=" + operation +
                ", value=" + value +
                '}';
    }
}
