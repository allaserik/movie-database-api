package com.moviedb_api.service;

import java.util.Set;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.moviedb_api.dto.CreateOrPatchGenreDto;
import com.moviedb_api.dto.GenreFilterDto;
import com.moviedb_api.dto.GenreResponseDto;
import com.moviedb_api.entity.Genre;
import com.moviedb_api.entity.Movie;
import com.moviedb_api.exception.BadRequestException;
import com.moviedb_api.exception.ResourceAlreadyExistsException;
import com.moviedb_api.exception.ResourceNotFoundException;
import com.moviedb_api.repository.GenreRepository;
import com.moviedb_api.repository.MovieRepository;
import com.moviedb_api.repository.specification.GenreSpecification;

@Service
public class GenreService {

    private static final Logger logger = LoggerFactory.getLogger(GenreService.class);

    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;

    public GenreService(GenreRepository genreRepository, MovieRepository movieRepository,
            ModelMapper modelMapper) {
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
    }

    public List<GenreResponseDto> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        if (genres.size() == 0) {
            logger.info("No genres found in the database.");
            return List.of();
        }

        logger.info("Getting all genres:" + genres);
        return genres.stream().map(genre -> modelMapper.map(genre, GenreResponseDto.class))
                .collect(Collectors.toList());
    }

    public Page<Genre> getGenresByFilter(GenreFilterDto genreFilter) {
        logger.info("getGenresByFilter filter: " + genreFilter);
        Pageable pageable = genreFilter.getPageable();
        Specification<Genre> spec = GenreSpecification.getGenresByFilter(genreFilter);
        Page<Genre> GenrePage = genreRepository.findAll(spec, pageable);

        logger.info("getGenresByFilter result data: " + GenrePage.getContent());
        return GenrePage;
    }

    public GenreResponseDto getGenreById(Long id) throws ResourceNotFoundException {
        Genre genre = getGenreByIdOrThrowError(id);
        logger.info("getGenreById data: " + genre);
        return modelMapper.map(genre, GenreResponseDto.class);
    }

    // get genre movies
    public Set<Movie> getGenreMovies(Long genreId) throws ResourceNotFoundException {
        Genre genre = getGenreByIdOrThrowError(genreId);
        logger.info("Found Genre: " + genre);
        Set<Movie> genreMovies = movieRepository.findByGenres_Id(genreId);
        logger.info("Found Genres: " + genreMovies);
        return genreMovies;

    }

    @Transactional
    public GenreResponseDto createGenre(CreateOrPatchGenreDto genreDto)
            throws ResourceAlreadyExistsException {
        if (genreRepository.existsByNameContainingIgnoreCase(genreDto.getName())) {
            throw new ResourceAlreadyExistsException("Genre already exists: " + genreDto.getName());
        }

        Genre genre = new Genre();
        genre.setName(genreDto.getName().toLowerCase());
        Genre savedGenre = genreRepository.save(genre);

        return modelMapper.map(savedGenre, GenreResponseDto.class);
    }

    public List<GenreResponseDto> createBatchGenres(Set<CreateOrPatchGenreDto> genresDto) {
        Set<Genre> createdGenres = findOrCreateGenres(genresDto);
        return createdGenres.stream().map(genre -> modelMapper.map(genre, GenreResponseDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public GenreResponseDto updateGenre(Long id, CreateOrPatchGenreDto genreDto)
            throws ResourceNotFoundException, ResourceAlreadyExistsException {
        Genre existingGenre = getGenreByIdOrThrowError(id);

        String newName = genreDto.getName().toLowerCase();

        if (genreRepository.existsByNameContainingIgnoreCase(newName)) {
            throw new ResourceAlreadyExistsException("Genre already exists: " + newName);
        }

        existingGenre.setName(newName);

        Genre updatedGenre = genreRepository.save(existingGenre);
        return modelMapper.map(updatedGenre, GenreResponseDto.class);
    }


    @Transactional
    public void deleteGenre(Long id, boolean force)
            throws ResourceNotFoundException, BadRequestException {
        // Check if the genre exists
        Genre genre = getGenreByIdOrThrowError(id);
        logger.info("Found genre: " + genre);

        // Find all movies associated with this genre
        Set<Movie> relatedMovies = movieRepository.findByGenres_Id(id);
        logger.info("Found related movies: " + relatedMovies);

        if (!force && !relatedMovies.isEmpty()) {
            // If force is false, return an error message listing the related movies
            String movieTitles =
                    relatedMovies.stream().map(Movie::getTitle).collect(Collectors.joining(", "));
            throw new BadRequestException(
                    "Genre has related movies and cannot be deleted: " + movieTitles);
        }
        // If force is true, remove genre from each related movie's genre set
        relatedMovies.forEach(movie -> movie.getGenres().remove(genre));
        movieRepository.saveAll(relatedMovies); // Save updates for each movie

        genre.getMovies().clear();

        // Delete the genre after detaching from related movies
        logger.info("Deleting genre " + id);
        genreRepository.delete(genre);
        return;
    }

    public Genre getGenreByIdOrThrowError(Long id) throws ResourceNotFoundException {
        return genreRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Genre with id " + id + " not found."));
    }

    public Set<Genre> findOrCreateGenres(Set<CreateOrPatchGenreDto> genres) {
        return genres.stream()
                .map(genreDto -> findOrCreateGenreByName(genreDto.getName().toLowerCase()))
                .collect(Collectors.toSet());
    }


    @Transactional
    private Genre findOrCreateGenreByName(String genreName) {
        logger.info("Finding or creating genre: " + genreName);
        Optional<Genre> genre = genreRepository.findByNameContainingIgnoreCase(genreName);

        logger.info("Found genre: " + genre);
        return genre.orElseGet(() -> {
            return genreRepository.save(new Genre(genreName.toLowerCase()));
        });
    }

    // Find Genres by movie id
    public Set<Genre> findGenresByMovieId(Long movieId) {
        return genreRepository.findAllByMovies_Id(movieId);
    }

    // Save all genres
    public void saveAllGenres(Set<Genre> genres) {
        genreRepository.saveAll(genres);
    }

}
