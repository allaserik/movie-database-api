package com.moviedb_api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MovieDto extends BaseDto {
    private String title;
    private Integer releaseYear;
    private Integer duration;
}
