package ru.krestyankin.library.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.krestyankin.library.models.Author;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends MongoRepository<Author, String> {
    List<Author> findByFullname(String fullname);
    Optional<Author> findByFullnameAndDateOfBirth(String fullname, Date dateOfBirth);
}
