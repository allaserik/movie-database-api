package com.moviedb_api.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import com.moviedb_api.dto.GenreFilterDto;
import com.moviedb_api.entity.Genre;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class GenreSpecification {

    public static Specification<Genre> getGenresByFilter(GenreFilterDto filter) {
        return (Root<Genre> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Apply name filter
            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"));
            }

            // Apply movieId filter
            if (filter.getMovieId() != null) {
                predicates.add(builder.equal(root.join("movies").get("id"), filter.getMovieId()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
