package com.moviedb_api.dto;

import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MovieResponseDto extends BaseDto {

    private String title;
    private Integer releaseYear;
    private Integer duration;
    private Set<ActorDto> actors;
    private Set<GenreResponseDto> genres;

}
