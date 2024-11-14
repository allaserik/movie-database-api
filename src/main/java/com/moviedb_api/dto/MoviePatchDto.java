package com.moviedb_api.dto;

import java.util.Set;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MoviePatchDto {
    @Size(min = 2, message = "Title should have at least 2 characters")
    private String title;

    @Min(value = 1900, message = "Release year should be after 1900")
    @Max(value = 2100, message = "Release year should be less than 2100")
    private Integer releaseYear;

    @Min(value = 1, message = "Duration should be greater than 0")
    private Integer duration;

    @Valid
    private Set<ActorCreateDto> actors;

    @Valid
    private Set<CreateOrPatchGenreDto> genres;

    private Long[] actorIds;
    private Long[] genreIds;
}
