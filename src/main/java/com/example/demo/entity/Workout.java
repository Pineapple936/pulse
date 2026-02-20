package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workout")
@Data
@NoArgsConstructor
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL)
    private List<Exercise> exercises;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Workout(String name, LocalDateTime date, User user) {
        this.name = name;
        this.date = date;
        this.user = user;
    }
}
