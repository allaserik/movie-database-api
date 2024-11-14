package com.moviedb_api.repository;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.moviedb_api.entity.Movie;


@Repository
public interface MovieRepository
                extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

        // Check if movie with these properties already exists
        boolean existsByTitleAndReleaseYearAndDuration(String title, Integer releaseYear,
                        Integer duration);

        Set<Movie> findByGenres_Id(Long genreId);

        Set<Movie> findByActors_Id(long actorId);

        @EntityGraph(attributePaths = {"actors", "genres"})
        Optional<Movie> findWithAssociationsById(Long id);

        // List<Movie> findByReleaseYear(Integer releaseYear);

        // List<Movie> findByAssignedActors_Id(Long actorId);

        // List<Movie> findByAssignedGenres_Id(Long genreId, Pageable pageable);

        // List<Movie> findByReleaseYear(Integer releaseYear, Pageable pageable);

        // List<Movie> findByAssignedActors_Id(Long actorId, Pageable pageable);

        // List<Movie> findByAssignedGenres_IdAndReleaseYear(Long genreId, Integer releaseYear,
        // Pageable pageable);

        // List<Movie> findByAssignedGenres_IdAndAssignedActors_Id(Long genreId, Long actorId,
        // Pageable pageable);

        // List<Movie> findByReleaseYearAndAssignedActors_Id(Integer releaseYear, Long actorId,
        // Pageable pageable);

        // List<Movie> findByAssignedGenres_IdAndReleaseYearAndAssignedActors_Id(Long genreId,
        // Integer releaseYear, Long actorId, Pageable pageable);
}
