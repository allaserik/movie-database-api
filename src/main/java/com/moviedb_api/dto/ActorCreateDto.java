package com.moviedb_api.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ActorCreateDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, message = "Name should have at least 2 characters")
    private String name;

    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate should be in the past")
    private LocalDate birthDate;

}
