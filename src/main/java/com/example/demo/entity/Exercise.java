package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "exercise")
@Data
@NoArgsConstructor
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_id")
    @JsonIgnore
    private Workout workout;

    @ManyToOne
    @JoinColumn(name = "exercise_type_id")
    private ExerciseType exerciseType;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Exercise(Workout workout, ExerciseType exerciseType) {
        this.workout = workout;
        this.exerciseType = exerciseType;
    }
}
