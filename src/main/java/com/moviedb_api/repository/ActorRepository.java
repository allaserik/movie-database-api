package com.moviedb_api.repository;

import java.util.Optional;
import java.util.Set;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.moviedb_api.entity.Actor;

@Repository
public interface ActorRepository
                extends JpaRepository<Actor, Long>, JpaSpecificationExecutor<Actor> {

        boolean existsByNameContainingIgnoreCaseAndBirthDate(String name, LocalDate birthDate);


        @Query("SELECT a FROM Actor a WHERE a.name = :name AND a.birthDate = :birthDate ")
        Optional<Actor> findActorByNameContainingIgnoreCaseAndBirthDate(String name,
                        LocalDate birthDate);

        Set<Actor> findAllByMovies_Id(long movieId);

}
