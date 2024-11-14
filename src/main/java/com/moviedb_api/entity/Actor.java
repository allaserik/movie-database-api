package com.moviedb_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "actor")
public class Actor extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    // ManyToMany relationship with Movie entity
    @ManyToMany(mappedBy = "actors", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,
            CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    // @JsonIgnoreProperties("assignedActors")
    // @JsonIgnore
    private Set<Movie> movies = new HashSet<>();

    public Actor(String name, LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }
}
