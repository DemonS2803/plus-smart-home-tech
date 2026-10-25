package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "scenarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String hubId;
    private String name;

    @OneToMany(
            mappedBy = "scenario",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private List<ScenarioCondition> conditions = new ArrayList<>();

    @OneToMany(
            mappedBy = "scenario",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private List<ScenarioAction> actions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Scenario scenario = (Scenario) o;
        return Objects.equals(id, scenario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "id=" + id +
                ", hubId='" + hubId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
