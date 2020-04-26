package ru.krestyankin.library.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.krestyankin.library.models.Genre;

import java.util.Optional;

public interface GenreRepository extends MongoRepository<Genre, String> {
    Optional<Genre> findByName(String name);
}
