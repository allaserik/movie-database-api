package com.moviedb_api.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.moviedb_api.dto.MovieDto;
import com.moviedb_api.dto.MoviePatchDto;
import com.moviedb_api.dto.MovieCreateDto;
import com.moviedb_api.entity.Movie;
import com.moviedb_api.exception.ResourceAlreadyExistsException;
import com.moviedb_api.exception.ResourceNotFoundException;
import com.moviedb_api.service.MovieService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/movie")
public class MovieWebController {

    private final MovieService movieService;

    public MovieWebController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/index")
    public String showMovieList(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "movie/index"; // Maps to index.html
    }

    @GetMapping("/addmovie")
    public String showMovieEntryForm(Model model) {
        model.addAttribute("movie", new MovieDto());
        return "movie/add-movie"; // Maps to add-movie.html
    }

    @PostMapping("/addmovie")
    public String addGenre(@Valid MovieCreateDto movieDto, BindingResult result, Model model)
            throws ResourceAlreadyExistsException {
        if (result.hasErrors()) {
            return "add-movie";
        }
        movieService.createMovie(movieDto);
        return "redirect:/movie/index";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model)
            throws ResourceNotFoundException {
        Movie movie = movieService.getMovieById(id);
        model.addAttribute("movie", movie);
        return "movie/update-movie"; // Maps to update-movie.html
    }

    @PostMapping("/update/{id}")
    public String updateMovie(@PathVariable("id") long id, @Valid MoviePatchDto movieDto,
            BindingResult result) throws ResourceNotFoundException {
        if (result.hasErrors()) {
            return "movie/update-movie";
        }
        movieService.updateMovie(id, movieDto);
        return "redirect:/movie/index";
    }

    // @GetMapping("/delete/{id}")
    // public String deleteMovie(@PathVariable("id") long id, Model model)
    // throws ResourceNotFoundException {
    // MovieResponseDto movieDto = movieService.getMovieById(id);
    // model.addAttribute("movie", movieDto);
    // return "movie/delete-movie"; // Maps to delete-movie.html
    // }

}
