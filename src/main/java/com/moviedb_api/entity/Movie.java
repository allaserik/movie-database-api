package com.moviedb_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movie")
public class Movie extends BaseEntity {

        @Column(nullable = false)
        private String title;

        @Column(nullable = false)
        private Integer releaseYear;

        @Column(nullable = false)
        private Integer duration; // in minutes

        // Many-to-Many relationship with Actor entity
        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
                        CascadeType.REFRESH}, fetch = FetchType.LAZY)
        @JoinTable(name = "movie_actor", joinColumns = @JoinColumn(name = "movie_id"),
                        inverseJoinColumns = @JoinColumn(name = "actor_id"))
        // @JsonIgnoreProperties("assignedMovies") // Avoids recursive fetch of movies in Actor
        private Set<Actor> actors = new HashSet<>();

        // Many-to-Many relationship with Genre entity
        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
                        CascadeType.REFRESH}, fetch = FetchType.LAZY)
        @JoinTable(name = "movie_genre", joinColumns = @JoinColumn(name = "movie_id"),
                        inverseJoinColumns = @JoinColumn(name = "genre_id"))
        // @JsonIgnoreProperties("assignedMovies") // Avoids recursive fetch of movies in Genre
        private Set<Genre> genres = new HashSet<>();

        public Movie(String title, Integer releaseYear, Integer duration) {
                this.title = title;
                this.releaseYear = releaseYear;
                this.duration = duration;
        }
}
