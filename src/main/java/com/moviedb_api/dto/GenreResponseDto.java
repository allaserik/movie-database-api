package com.moviedb_api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GenreResponseDto extends BaseDto {
    private String name;

}
