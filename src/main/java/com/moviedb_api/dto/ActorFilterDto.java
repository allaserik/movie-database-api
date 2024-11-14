package com.moviedb_api.dto;

import java.time.LocalDate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.Data;

@Data
public class ActorFilterDto {
    String name;
    LocalDate birthDate;
    Long movieId;
    int page = 0;
    int size = 10;
    String sortBy = "name";

    public Pageable getPageable() {
        return PageRequest.of(page, size, Sort.by(sortBy));
    }
}
