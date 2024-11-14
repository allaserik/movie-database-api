package com.moviedb_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;
// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "genre")
public class Genre extends BaseEntity {

    @Column(nullable = false)
    private String name;

    // Many to many relationship with Movie entity
    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,
            CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    // @JsonIgnoreProperties("assignedGenres")
    // @JsonIgnore
    private Set<Movie> movies = new HashSet<>();

    public Genre(String name) {
        this.name = name;
    }
}
