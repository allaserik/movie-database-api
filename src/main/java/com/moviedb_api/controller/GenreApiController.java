package com.moviedb_api.controller;

import java.util.List;
import java.util.Set;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.moviedb_api.dto.GenreFilterDto;
import com.moviedb_api.dto.CreateOrPatchGenreDto;
import com.moviedb_api.dto.GenreResponseDto;
import com.moviedb_api.entity.Genre;
import com.moviedb_api.entity.Movie;
import com.moviedb_api.exception.BadRequestException;
import com.moviedb_api.exception.ResourceAlreadyExistsException;
import com.moviedb_api.exception.ResourceNotFoundException;
import com.moviedb_api.service.GenreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/genre")
public class GenreApiController {
    // private static final Logger logger = LoggerFactory.getLogger(GenreService.class);

    private final GenreService genreService;

    public GenreApiController(GenreService genreService) {
        this.genreService = genreService;
    }

    // CRUD endpoints


    // CRUD endpoints
    @GetMapping("")
    public ResponseEntity<Page<Genre>> getGenresByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long movieId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        GenreFilterDto genreFilter = new GenreFilterDto();
        genreFilter.setName(name);
        genreFilter.setMovieId(movieId);
        genreFilter.setPage(page);
        genreFilter.setSize(size);
        genreFilter.setSortBy(sortBy);
        return ResponseEntity.ok().body(genreService.getGenresByFilter(genreFilter));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<GenreResponseDto>> getAllGenres() {
        return ResponseEntity.ok().body(genreService.getAllGenres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponseDto> getGenreById(@PathVariable("id") Long genreId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(genreService.getGenreById(genreId));
    }

    @GetMapping("/{id}/movies")
    public ResponseEntity<Set<Movie>> getGenreMovies(@PathVariable("id") long genreId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(genreService.getGenreMovies(genreId));

    }



    @PostMapping("")
    public ResponseEntity<GenreResponseDto> createGenre(
            @Valid @RequestBody CreateOrPatchGenreDto genreDto)
            throws ResourceAlreadyExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.createGenre(genreDto));

    }

    @PostMapping("/batch")
    public ResponseEntity<List<GenreResponseDto>> createBatchGenres(
            @Valid @RequestBody Set<CreateOrPatchGenreDto> genres)
            throws ResourceAlreadyExistsException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(genreService.createBatchGenres(genres));
    }



    @PatchMapping("/{id}")
    public ResponseEntity<GenreResponseDto> updateGenre(@PathVariable Long id,
            @Valid @RequestBody CreateOrPatchGenreDto genreDto)
            throws ResourceNotFoundException, ResourceAlreadyExistsException {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(genreService.updateGenre(id, genreDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean force)
            throws ResourceNotFoundException, BadRequestException {
        genreService.deleteGenre(id, force);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

