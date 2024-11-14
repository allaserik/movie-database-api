package com.moviedb_api.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ActorPatchDto {

    @Size(min = 2, message = "Name should have at least 2 characters")
    private String name;

    @Past(message = "Birthdate should be in the past")
    private LocalDate birthDate;

}
