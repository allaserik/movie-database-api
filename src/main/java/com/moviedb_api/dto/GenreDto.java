package com.moviedb_api.dto;

import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GenreDto extends BaseDto {
    private String name;
    private Set<MovieDto> movies;
}
