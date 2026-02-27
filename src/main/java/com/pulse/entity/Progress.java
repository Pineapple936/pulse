package com.pulse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Data
@NoArgsConstructor
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "repetitions", nullable = false)
    private int repetitions;

    @Column(name = "sets", nullable = false)
    private int sets;

    @Column(name = "weight", nullable = false, scale = 2)
    private float weight;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    @JsonIgnore
    private Exercise exercise;

    @CreationTimestamp
    @JsonIgnore
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Progress(int repetitions, int sets, float weight, Exercise exercise) {
        this.repetitions = repetitions;
        this.sets = sets;
        this.weight = weight;
        this.exercise = exercise;
    }
}
