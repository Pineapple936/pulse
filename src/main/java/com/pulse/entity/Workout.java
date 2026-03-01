package com.pulse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pulse.entity.dto.WorkoutDetailsDto;
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
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Exercise> exercises;

    @CreationTimestamp
    @JsonIgnore
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Workout(User user, WorkoutDetailsDto dto) {
        if(dto.name() == null || dto.name().isBlank()) throw new IllegalArgumentException("Name cannot be empty or blank");
        if(dto.date() == null) throw new IllegalArgumentException("Date cannot be null");

        this.name = dto.name();
        this.date = dto.date();
        this.user = user;
    }
}
