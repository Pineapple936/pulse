package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int repetitions;
    private int sets;
    private float weight;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
