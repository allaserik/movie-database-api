package com.moviedb_api.repository.specification;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import com.moviedb_api.dto.MovieFilterDto;
import com.moviedb_api.entity.Movie;
import jakarta.persistence.criteria.*;

public class MovieSpecification {
    public static Specification<Movie> getMoviesByFilter(MovieFilterDto filter) {
        return (Root<Movie> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Apply genre filter
            if (filter.getGenreId() != null) {
                predicates.add(builder.equal(root.join("genres").get("id"), filter.getGenreId()));
            }

            // Apply year filter
            if (filter.getYear() != null) {
                predicates.add(builder.equal(root.get("releaseYear"), filter.getYear()));
            }

            // Apply actor filter
            if (filter.getActorId() != null) {
                predicates.add(builder.equal(root.join("actors").get("id"), filter.getActorId()));
            }

            // Apply name filter (e.g., title or partial title match)
            if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("title")),
                        "%" + filter.getTitle().toLowerCase() + "%"));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
