package com.moviedb_api.controller;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.moviedb_api.dto.*;
import com.moviedb_api.entity.Actor;
import com.moviedb_api.entity.Genre;
import com.moviedb_api.entity.Movie;
import com.moviedb_api.exception.BadRequestException;
import com.moviedb_api.exception.ResourceAlreadyExistsException;
import com.moviedb_api.exception.ResourceNotFoundException;
import com.moviedb_api.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;



@RestController
@RequestMapping("/api/v1/movie")
public class MovieApiController {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieService movieService;

    public MovieApiController(MovieService movieService) {
        this.movieService = movieService;
    }



    @GetMapping("")
    public ResponseEntity<Page<Movie>> getMoviesByFilter(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) @Min(1900) @Max(2100) Integer year,
            @RequestParam(required = false) Long actorId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "title") String sortBy) {
        MovieFilterDto movieFilter = new MovieFilterDto();
        movieFilter.setTitle(title);
        movieFilter.setGenreId(genreId);
        movieFilter.setYear(year);
        movieFilter.setActorId(actorId);
        movieFilter.setPage(page);
        movieFilter.setSize(size);
        movieFilter.setSortBy(sortBy);

        return ResponseEntity.ok().body(movieService.getMoviesByFilter(movieFilter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(movieService.getMovieById(id));
    }

    @GetMapping("/{movieId}/actors")
    public ResponseEntity<Set<Actor>> getActorsInMovie(@PathVariable Long movieId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(movieService.getActorsInMovie(movieId));
    }

    @GetMapping("/{movieId}/genres")
    public ResponseEntity<Set<Genre>> getGenresInMovie(@PathVariable Long movieId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(movieService.getGenresInMovie(movieId));
    }

    @PostMapping("")
    public ResponseEntity<MovieResponseDto> createMovie(@Valid @RequestBody MovieCreateDto movieDto)
            throws ResourceAlreadyExistsException {
        logger.info("Creating new movie with input: " + movieDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(movieDto));
    }



    @PatchMapping("/{id}")
    public ResponseEntity<MovieResponseDto> updateMovie(@PathVariable Long id,
            @Valid @RequestBody MoviePatchDto movieDto) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(movieService.updateMovie(id, movieDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean force)
            throws ResourceNotFoundException, BadRequestException {
        movieService.deleteMovie(id, force);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
