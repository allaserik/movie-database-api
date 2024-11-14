package com.moviedb_api.dto;

import java.util.Set;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MovieCreateDto {
    @NotBlank
    @Size(min = 2, message = "Title should have at least 2 characters")
    private String title;

    @NotNull(message = "Release year is required")
    @Min(value = 1888, message = "Release year should be after 1888")
    private Integer releaseYear;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration should be greater than 0")
    private Integer duration;

    @Valid
    private Set<ActorCreateDto> actors;

    @Valid
    private Set<CreateOrPatchGenreDto> genres;

    private Long[] actorIds;
    private Long[] genreIds;
}
