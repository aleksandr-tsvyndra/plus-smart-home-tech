package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Table(name = "sensors")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString
public class Sensor {
    @Id
    private String id;

    @Column(name = "hub_id")
    private String hubId;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "scenario_conditions",
            joinColumns = @JoinColumn(name = "sensor_id"),
            inverseJoinColumns = @JoinColumn(name = "scenario_id")
    )
    private Set<Scenario> scenarioConditions = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "scenario_actions",
            joinColumns = @JoinColumn(name = "sensor_id"),
            inverseJoinColumns = @JoinColumn(name = "scenario_id")
    )
    private Set<Scenario> scenarioActions = new HashSet<>();
}
