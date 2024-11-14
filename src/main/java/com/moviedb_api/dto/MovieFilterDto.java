package com.moviedb_api.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.Data;

@Data
public class MovieFilterDto {
    String title;
    Long genreId;
    Integer year;
    Long actorId;
    int page = 0;
    int size = 10;
    String sortBy = "title";

    public Pageable getPageable() {
        return PageRequest.of(page, size, Sort.by(sortBy));
    }
}
