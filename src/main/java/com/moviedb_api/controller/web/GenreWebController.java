package com.moviedb_api.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.moviedb_api.dto.CreateOrPatchGenreDto;
import com.moviedb_api.dto.GenreDto;
import com.moviedb_api.dto.GenreResponseDto;
import com.moviedb_api.service.GenreService;
import com.moviedb_api.exception.ResourceAlreadyExistsException;
import com.moviedb_api.exception.ResourceNotFoundException;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/genre")
public class GenreWebController {

    private final GenreService genreService;

    public GenreWebController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/index")
    public String showGenreList(Model model) {
        model.addAttribute("genres", genreService.getAllGenres());
        return "genre/index"; // Maps to index.html
    }

    @GetMapping("/addgenre")
    public String showGenreEntryForm(Model model) {
        model.addAttribute("genre", new GenreDto());
        return "genre/add-genre"; // Maps to add-genre.html
    }

    @PostMapping("/addgenre")
    public String addGenre(@Valid CreateOrPatchGenreDto genreDto, BindingResult result, Model model)
            throws ResourceAlreadyExistsException {
        if (result.hasErrors()) {
            return "add-genre";
        }
        genreService.createGenre(genreDto);
        return "redirect:/genre/index";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model)
            throws ResourceNotFoundException {
        GenreResponseDto genre = genreService.getGenreById(id);
        model.addAttribute("genre", genre);
        return "genre/update-genre"; // Maps to update-genre.html
    }

    @PostMapping("/update/{id}")
    public String updateGenre(@PathVariable("id") long id, @Valid CreateOrPatchGenreDto genreDto,
            BindingResult result) throws ResourceNotFoundException, ResourceAlreadyExistsException {
        if (result.hasErrors()) {
            return "genre/update-genre";
        }
        genreService.updateGenre(id, genreDto);
        return "redirect:/genre/index";
    }

    // @GetMapping("/delete/{id}")
    // public String deleteGenre(@PathVariable("id") long id, @("force") boolean force {
    // genreService.deleteGenre(id, force);
    // return "redirect:/genre/index";
    // }

}
