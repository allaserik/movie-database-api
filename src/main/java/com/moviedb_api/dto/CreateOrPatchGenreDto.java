package com.moviedb_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateOrPatchGenreDto {
    @NotBlank(message = "Name is required")
    @Size(min = 2, message = "Name should have at least 2 characters")
    private String name;
}
