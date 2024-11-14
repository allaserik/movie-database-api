package com.moviedb_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.moviedb_api.entity.Genre;
import java.util.Optional;
import java.util.Set;

@Repository
public interface GenreRepository
        extends JpaRepository<Genre, Long>, JpaSpecificationExecutor<Genre> {

    boolean existsByNameContainingIgnoreCase(String name);

    Optional<Genre> findByNameContainingIgnoreCase(String name);

    Set<Genre> findAllByMovies_Id(long movieId);

}
