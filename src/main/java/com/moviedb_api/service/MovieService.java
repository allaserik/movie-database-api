package com.moviedb_api.service;

import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.moviedb_api.dto.MoviePatchDto;
import com.moviedb_api.dto.MovieCreateDto;
import com.moviedb_api.dto.MovieFilterDto;
import com.moviedb_api.dto.MovieResponseDto;
import com.moviedb_api.entity.Actor;
import com.moviedb_api.entity.Genre;
import com.moviedb_api.entity.Movie;
import com.moviedb_api.exception.BadRequestException;
import com.moviedb_api.exception.ResourceAlreadyExistsException;
import com.moviedb_api.exception.ResourceNotFoundException;
import com.moviedb_api.repository.MovieRepository;
import com.moviedb_api.repository.specification.MovieSpecification;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;
    private final GenreService genreService;
    private final ActorService actorService;

    public MovieService(MovieRepository movieRepository, ModelMapper modelMapper,
            GenreService genreService, ActorService actorService) {
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
        this.actorService = actorService;
        this.genreService = genreService;
    }

    @Transactional
    public MovieResponseDto createMovie(MovieCreateDto movieDto)
            throws ResourceAlreadyExistsException {
        logger.info("Creating new movie with input: " + movieDto);
        if (movieRepository.existsByTitleAndReleaseYearAndDuration(movieDto.getTitle(),
                movieDto.getReleaseYear(), movieDto.getDuration())) {
            throw new ResourceAlreadyExistsException(
                    "Movie already exists: \n" + movieDto.toString());
        }

        Movie newMovie =
                new Movie(movieDto.getTitle(), movieDto.getReleaseYear(), movieDto.getDuration());

        if (movieDto.getActors() != null) {
            Set<Actor> actors = actorService.findOrCreateActors(movieDto.getActors());
            newMovie.setActors(actors);
        }

        if (movieDto.getGenres() != null) {
            Set<Genre> genres = genreService.findOrCreateGenres(movieDto.getGenres());
            newMovie.setGenres(genres);
        }

        logger.info("Saving movie: " + newMovie);
        Movie savedMovie = movieRepository.save(newMovie);
        logger.info("Saved movie data: " + savedMovie);
        return modelMapper.map(savedMovie, MovieResponseDto.class);
    }

    public Set<MovieResponseDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        logger.info("getAllMovies findAll : " + movies);
        return movies.stream().map(movie -> modelMapper.map(movie, MovieResponseDto.class))
                .collect(Collectors.toSet());
    }

    public Page<Movie> getMoviesByFilter(MovieFilterDto filter) {
        logger.info("getMoviesByFilter filter: " + filter);
        Pageable pageable = filter.getPageable();
        Specification<Movie> spec = MovieSpecification.getMoviesByFilter(filter);
        Page<Movie> moviePage = movieRepository.findAll(spec, pageable);

        logger.info("getMoviesByFilter result data: " + moviePage.getContent());
        return moviePage;
    }

    public Set<Actor> getActorsInMovie(Long movieId) throws ResourceNotFoundException {
        Movie movie = getMovieByIdOrThrowError(movieId);
        logger.info("Found movie: " + movie);
        Set<Actor> movieActors = actorService.findActorsByMovieId(movieId);
        logger.info("Found actors: " + movieActors);
        return movieActors;

    }

    public Set<Genre> getGenresInMovie(Long movieId) throws ResourceNotFoundException {
        Movie movie = getMovieByIdOrThrowError(movieId);
        logger.info("Found movie: " + movie);
        Set<Genre> movieGenres = genreService.findGenresByMovieId(movieId);
        logger.info("Found actors: " + movieGenres);
        return movieGenres;

    }

    public Movie getMovieById(Long id) throws ResourceNotFoundException {
        Movie movie = getMovieByIdOrThrowError(id);
        logger.info("getMovieById data: " + movie);
        return movie;
    }

    public Page<Movie> getGenreMovies(long id) {
        MovieFilterDto movieFilter = new MovieFilterDto();
        movieFilter.setGenreId(id);
        logger.info("getGenreMovies: " + movieFilter);
        Page<Movie> movieList = getMoviesByFilter(movieFilter);
        logger.info("getMoviesByFilter: " + movieList);

        return movieList;
    }

    @Transactional
    public MovieResponseDto updateMovie(Long id, MoviePatchDto movieDto)
            throws ResourceNotFoundException {
        // Debug log trying to update movie
        logger.info("Updating movie with id: " + id + " " + movieDto);
        Movie existingMovie = getMovieByIdOrThrowError(id);

        // Update non-null fields
        if (movieDto.getTitle() != null) {
            existingMovie.setTitle(movieDto.getTitle());
        }
        if (movieDto.getReleaseYear() != null) {
            existingMovie.setReleaseYear(movieDto.getReleaseYear());
        }
        if (movieDto.getDuration() != null) {
            existingMovie.setDuration(movieDto.getDuration());
        }

        // Update relationships
        if (movieDto.getActors() != null) {
            Set<Actor> actors = actorService.findOrCreateActors(movieDto.getActors());
            existingMovie.setActors(actors);
        }

        if (movieDto.getGenres() != null) {
            Set<Genre> genres = genreService.findOrCreateGenres(movieDto.getGenres());
            existingMovie.setGenres(genres);
        }

        logger.info("Saving movie: " + existingMovie);
        Movie updatedMovie = movieRepository.save(existingMovie);
        logger.info("Saved updated movie: " + updatedMovie);
        return modelMapper.map(updatedMovie, MovieResponseDto.class);
    }

    @Transactional
    public void deleteMovie(Long movieId, boolean force)
            throws ResourceNotFoundException, BadRequestException {
        // Check if movie exists
        Movie movie = getMovieByIdOrThrowError(movieId);
        logger.info("Found movie: " + movie);

        // Find all actors associated with this movie
        Set<Actor> movieActors = actorService.findActorsByMovieId(movieId);
        logger.info("Found actors: " + movieActors);

        // Find all genres associated with this movie
        Set<Genre> movieGenres = genreService.findGenresByMovieId(movieId);
        logger.info("Found genres: " + movieGenres);

        if (!force && (!movieActors.isEmpty() || !movieGenres.isEmpty())) {
            // If force is false, return an error message listing the related actors and genres
            String actorNames =
                    movieActors.stream().map(Actor::getName).collect(Collectors.joining(", "));
            String genreNames =
                    movieGenres.stream().map(Genre::getName).collect(Collectors.joining(", "));
            throw new BadRequestException(
                    "Cannot delete movie with id " + movieId + ". It is associated with actors: "
                            + actorNames + " and genres: " + genreNames);
        }

        // If force is true, clear associations
        movieActors.forEach(actor -> actor.getMovies().remove(movie));
        movieGenres.forEach(genre -> genre.getMovies().remove(movie));
        actorService.saveAllActors(movieActors);
        genreService.saveAllGenres(movieGenres);

        movie.getActors().clear();
        movie.getGenres().clear();
        movieRepository.save(movie);
        // Delete movie
        try {
            logger.info("Deleting movie: " + movieId);
            movieRepository.delete(movie);
            logger.info("Movie deleted successfully: " + movieId);
        } catch (Exception e) {
            logger.error("Error deleting movie: " + movie, e);
            throw new RuntimeException("Failed to delete movie with id " + movieId, e);
        }
    }

    // Other methods
    public Movie getMovieByIdOrThrowError(Long id) throws ResourceNotFoundException {
        return movieRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Movie with id " + id + " not found."));
    }

    public Movie getMovieWithAssociationsByIdOrThrowError(Long id)
            throws ResourceNotFoundException {
        return movieRepository.findWithAssociationsById(id).orElseThrow(
                () -> new ResourceNotFoundException("Movie with id " + id + " not found."));
    }

    // Find all movies by genre id
    public Set<Movie> findMoviesByGenreId(Long genreId) {
        return movieRepository.findByGenres_Id(genreId);
    }

    // Save all movies
    public void saveAllMovies(Set<Movie> movies) {
        movieRepository.saveAll(movies);
    }



}
