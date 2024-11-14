package com.moviedb_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.moviedb_api.dto.ActorCreateDto;
import com.moviedb_api.dto.ActorFilterDto;
import com.moviedb_api.dto.ActorPatchDto;
import com.moviedb_api.dto.ActorResponseDto;
import com.moviedb_api.entity.Actor;
import com.moviedb_api.entity.Movie;
import com.moviedb_api.exception.BadRequestException;
import com.moviedb_api.exception.ResourceAlreadyExistsException;
import com.moviedb_api.exception.ResourceNotFoundException;
import com.moviedb_api.service.ActorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.Set;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/actor")
public class ActorApiController {
    // private static final Logger logger = LoggerFactory.getLogger(ActorService.class);

    private final ActorService actorService;

    public ActorApiController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorResponseDto> getActorById(@PathVariable("id") Long actorId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(actorService.getActorById(actorId));
    }

    // CRUD endpoints
    @GetMapping("")
    public ResponseEntity<Page<Actor>> getActorsByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate birthDate,
            @RequestParam(required = false) Long movieId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        ActorFilterDto actorFilter = new ActorFilterDto();
        actorFilter.setName(name);
        actorFilter.setBirthDate(birthDate);
        actorFilter.setMovieId(movieId);
        actorFilter.setPage(page);
        actorFilter.setSize(size);
        actorFilter.setSortBy(sortBy);

        return ResponseEntity.ok().body(actorService.getActorsByFilter(actorFilter));
    }

    @GetMapping("/{id}/movies")
    public ResponseEntity<Set<Movie>> getActorMovies(@PathVariable("id") long actorId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(actorService.getActorMovies(actorId));

    }

    @PostMapping("")
    public ResponseEntity<ActorResponseDto> createActor(@Valid @RequestBody ActorCreateDto actorDto)
            throws ResourceAlreadyExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.createActor(actorDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ActorResponseDto> updateActor(@PathVariable Long id,
            @Valid @RequestBody ActorPatchDto actorDto) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(actorService.updateActor(id, actorDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean force)
            throws ResourceNotFoundException, BadRequestException {
        actorService.deleteActor(id, force);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
}
