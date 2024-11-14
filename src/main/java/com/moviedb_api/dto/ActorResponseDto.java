package com.moviedb_api.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActorResponseDto extends BaseDto {
    private String name;
    private LocalDate birthDate;
    private Set<MovieDto> movies;
}
