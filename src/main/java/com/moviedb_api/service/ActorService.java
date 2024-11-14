package com.moviedb_api.service;

import java.util.Set;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.moviedb_api.dto.ActorCreateDto;
import com.moviedb_api.dto.ActorFilterDto;
import com.moviedb_api.dto.ActorPatchDto;
import com.moviedb_api.dto.ActorResponseDto;
import com.moviedb_api.entity.Actor;
import com.moviedb_api.entity.Movie;
import com.moviedb_api.exception.BadRequestException;
import com.moviedb_api.exception.ResourceAlreadyExistsException;
import com.moviedb_api.exception.ResourceNotFoundException;
import com.moviedb_api.repository.ActorRepository;
import com.moviedb_api.repository.MovieRepository;
import com.moviedb_api.repository.specification.ActorSpecification;

@Service
public class ActorService {
    private static final Logger logger = LoggerFactory.getLogger(ActorService.class);

    private final ActorRepository actorRepository;
    private final ModelMapper modelMapper;
    private final MovieRepository movieRepository;

    public ActorService(ActorRepository actorRepository, ModelMapper modelMapper,
            MovieRepository movieRepository) {
        this.actorRepository = actorRepository;
        this.modelMapper = modelMapper;
        this.movieRepository = movieRepository;
    }

    public List<ActorResponseDto> getAllActors() {
        List<Actor> actors = actorRepository.findAll();
        logger.info("Find all actors data: " + actors);
        return actorRepository.findAll().stream()
                .map(actor -> modelMapper.map(actor, ActorResponseDto.class))
                .collect(Collectors.toList());

    }

    public Page<Actor> getActorsByFilter(ActorFilterDto actorFilter) {
        logger.info("getActorsByFilter filter: " + actorFilter);
        Pageable pageable = actorFilter.getPageable();
        Specification<Actor> spec = ActorSpecification.getActorsByFilter(actorFilter);
        Page<Actor> actorPage = actorRepository.findAll(spec, pageable);

        logger.info("getActorsByFilter result data: " + actorPage.getContent());
        return actorPage;
    }


    public ActorResponseDto getActorById(Long id) throws ResourceNotFoundException {
        Actor actor = getActorByIdOrThrowError(id);
        logger.info("getActorById data: " + actor);
        return modelMapper.map(actor, ActorResponseDto.class);
    }

    // get actor movies
    public Set<Movie> getActorMovies(Long actorId) throws ResourceNotFoundException {
        Actor actor = getActorByIdOrThrowError(actorId);
        logger.info("Found actor: " + actor);
        Set<Movie> actorMovies = movieRepository.findByActors_Id(actorId);
        logger.info("Found actors: " + actorMovies);
        return actorMovies;

    }

    @Transactional
    public ActorResponseDto createActor(ActorCreateDto actorDto)
            throws ResourceAlreadyExistsException {
        if (actorRepository.existsByNameContainingIgnoreCaseAndBirthDate(actorDto.getName(),
                actorDto.getBirthDate())) {
            throw new ResourceAlreadyExistsException(
                    "Actor already exists: " + actorDto.getName() + ", " + actorDto.getBirthDate());
        }

        Actor actor = new Actor();
        actor = modelMapper.map(actorDto, Actor.class);
        actor = actorRepository.save(actor);

        logger.info("Saved actor: " + actor);
        return modelMapper.map(actor, ActorResponseDto.class);
    }

    @Transactional
    public ActorResponseDto updateActor(Long id, ActorPatchDto actorDto)
            throws ResourceNotFoundException {
        // Check if actor exists before updating
        Actor existingActor = getActorByIdOrThrowError(id);

        // Only update non-null fields in the DTO
        if (actorDto.getName() != null) {
            String actorName = actorDto.getName().toString();
            existingActor.setName(actorName);
        }

        if (actorDto.getBirthDate() != null) {
            LocalDate actorBirthDate = actorDto.getBirthDate();
            existingActor.setBirthDate(actorBirthDate);
        }

        // Save the updated actor entity
        Actor updatedActor = actorRepository.save(existingActor);
        return modelMapper.map(updatedActor, ActorResponseDto.class);
    }

    @Transactional
    public void deleteActor(Long id, boolean force)
            throws ResourceNotFoundException, BadRequestException {
        // Check if the actor exists
        Actor actor = getActorByIdOrThrowError(id);
        logger.info("Found actor: " + actor);

        // Find all movies associated with this actor
        Set<Movie> relatedMovies = movieRepository.findByActors_Id(id);

        if (!force && !relatedMovies.isEmpty()) {

            // If force is false, return an error message listing the related movies
            String movieTitles =
                    relatedMovies.stream().map(Movie::getTitle).collect(Collectors.joining(", "));
            throw new BadRequestException(
                    "Actor has related movies and cannot be deleted: " + movieTitles);
        }

        // If force is true, remove actor from each related movie's actor set
        relatedMovies.forEach(movie -> movie.getActors().remove(actor));
        movieRepository.saveAll(relatedMovies); // Save updates for each movie

        actor.getMovies().clear();

        // Delete the actor after detaching from related movies
        logger.info("Deleting actor " + id);
        actorRepository.delete(actor);
        return;
    }


    public Actor getActorByIdOrThrowError(Long id) throws ResourceNotFoundException {
        return actorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Actor with id " + id + " not found."));
    }


    public Set<Actor> findOrCreateActors(Set<ActorCreateDto> actors) {
        return actors.stream()
                .map(actorDto -> findOrCreateActorByNameAndBirthDate(actorDto.getName(),
                        actorDto.getBirthDate()))
                .collect(Collectors.toSet());
    }

    private Actor findOrCreateActorByNameAndBirthDate(String name, LocalDate birthDate) {
        logger.info("Finding or creating actor: " + name);
        Optional<Actor> actor =
                actorRepository.findActorByNameContainingIgnoreCaseAndBirthDate(name, birthDate);

        logger.info("Found actor: " + actor);
        return actor.orElseGet(() -> {
            return actorRepository.save(new Actor(name, birthDate));
        });
    }

    // Find actors by movie id
    public Set<Actor> findActorsByMovieId(Long movieId) {
        return actorRepository.findAllByMovies_Id(movieId);
    }

    // Save all actors
    public void saveAllActors(Set<Actor> actors) {
        actorRepository.saveAll(actors);
    }
}
