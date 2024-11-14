package com.moviedb_api.controller.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.moviedb_api.dto.ActorCreateDto;
import com.moviedb_api.dto.ActorDto;
import com.moviedb_api.dto.ActorPatchDto;
import com.moviedb_api.dto.ActorResponseDto;
import com.moviedb_api.exception.ResourceAlreadyExistsException;
import com.moviedb_api.exception.ResourceNotFoundException;
import com.moviedb_api.service.ActorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/actor")
public class ActorWebController {
    private static final Logger logger = LoggerFactory.getLogger(ActorService.class);

    private final ActorService actorService;

    public ActorWebController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping("/index")
    public String showActorList(Model model) {
        model.addAttribute("actors", actorService.getAllActors());
        return "actor/index"; // Maps to index.html
    }

    @GetMapping("/addactor")
    public String showActorEntryForm(Model model) {
        model.addAttribute("actor", new ActorDto()); // Creates a new empty ActorDto instance for
                                                     // the form.
        return "actor/add-actor"; // Maps to add-actor.html
    }

    @PostMapping("/addactor")
    public String addActor(@Valid ActorCreateDto actorDto, BindingResult result, Model model)
            throws ResourceAlreadyExistsException {
        if (result.hasErrors()) {
            return "add-actor";
        }
        actorService.createActor(actorDto);
        return "redirect:/actor/index";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model)
            throws ResourceNotFoundException {
        logger.info("Updating actor with id: {}", id);
        ActorResponseDto actorDto = actorService.getActorById(id);
        model.addAttribute("actor", actorDto);
        return "actor/update-actor"; // Maps to update-actor.html
    }

    @PostMapping("/update/{id}")
    public String updateActor(@PathVariable("id") long id,
            @ModelAttribute("actor") @Valid ActorPatchDto actorDto, BindingResult result,
            Model model) throws ResourceNotFoundException {
        if (result.hasErrors()) {
            model.addAttribute("actor", actorDto); // In case of errors, return the form with errors
            return "actor/update-actor";
        }
        actorService.updateActor(id, actorDto);
        return "redirect:/actor/index";
    }

    // @GetMapping("/delete/{id}")
    // public String deleteActor(@PathVariable("id") long id) throws ResourceNotFoundException {
    // actorService.deleteActor(id);
    // return "redirect:/actor/index";
    // }

}
