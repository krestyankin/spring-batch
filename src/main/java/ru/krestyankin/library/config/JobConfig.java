package ru.krestyankin.library.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.krestyankin.library.models.*;
import ru.krestyankin.library.repositories.AuthorRepository;
import ru.krestyankin.library.repositories.BookRepository;
import ru.krestyankin.library.repositories.GenreRepository;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class JobConfig {
    private static final int CHUNK_SIZE = 5;
    public static final String CONVERT_LIBRARY_JOB_NAME = "convertLibraryJob";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @StepScope
    @Bean
    public JpaPagingItemReader<GenreJpa> genresReader(EntityManager entityManager) {
        return new JpaPagingItemReaderBuilder<GenreJpa>()
                .name("genresReader")
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .queryString("select g from GenreJpa g")
                .build();
    }

    @StepScope
    @Bean
    public JpaPagingItemReader<AuthorJpa> authorsReader(EntityManager entityManager) {
        return new JpaPagingItemReaderBuilder<AuthorJpa>()
                .name("authorsReader")
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .queryString("select a from AuthorJpa a")
                .build();
    }

    @StepScope
    @Bean
    public JpaPagingItemReader<BookJpa> booksReader(EntityManager entityManager) {
        return new JpaPagingItemReaderBuilder<BookJpa>()
                .name("booksReader")
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .queryString("select distinct b from BookJpa b join fetch b.authors join fetch b.genres")
                .build();
    }

    @StepScope
    @Bean
    public JpaPagingItemReader<CommentJpa> commentsReader(EntityManager entityManager) {
        return new JpaPagingItemReaderBuilder<CommentJpa>()
                .name("commentsReader")
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .queryString("select c from CommentJpa c")
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<GenreJpa, Genre> genreConverter() {
        return g -> {
            Genre genre = new Genre();
            genre.setName(g.getName());
            return genre;
        };
    }

    @StepScope
    @Bean
    public ItemProcessor<AuthorJpa, Author> authorConverter() {
        return a -> {
            Author author = new Author();
            author.setFullname(a.getFullname());
            author.setDateOfBirth(a.getDateOfBirth());
            return author;
        };
    }

    @StepScope
    @Bean
    public ItemProcessor<BookJpa, Book> bookConverter(GenreRepository genreRepository, AuthorRepository authorRepository) {
        return b -> {
            Book book=new Book();
            book.setTitle(b.getTitle());
            List<Author> authors = new ArrayList<>();
            List<Genre> genres = new ArrayList<>();
            for (AuthorJpa a: b.getAuthors()) {
                authors.add(authorRepository.findByFullnameAndDateOfBirth(a.getFullname(), a.getDateOfBirth()).get());
            }
            for (GenreJpa g: b.getGenres()) {
                genres.add(genreRepository.findByName(g.getName()).get());
            }
            book.setAuthors(authors);
            book.setGenres(genres);
            return book;
        };
    }

    @StepScope
    @Bean
    public ItemProcessor<CommentJpa, Comment> commentConverter(BookRepository bookRepository) {
        return c -> {
            Comment comment = new Comment();
            comment.setText(c.getText());
            comment.setBook(bookRepository.findByTitleIsLike(c.getBook().getTitle()).get(0));
            return comment;
        };
    }

    @StepScope
    @Bean
    public MongoItemWriter<Genre> genresMongoWriter(MongoOperations mongoOperations) {
        return new MongoItemWriterBuilder<Genre>()
                .template(mongoOperations)
                .collection("genres")
                .build();
    }

    @StepScope
    @Bean
    public MongoItemWriter<Author> authorsMongoWriter(MongoOperations mongoOperations) {
        return new MongoItemWriterBuilder<Author>()
                .template(mongoOperations)
                .collection("authors")
                .build();
    }

    @StepScope
    @Bean
    public MongoItemWriter<Book> booksMongoWriter(MongoOperations mongoOperations) {
        return new MongoItemWriterBuilder<Book>()
                .template(mongoOperations)
                .collection("books")
                .build();
    }

    @StepScope
    @Bean
    public MongoItemWriter<Comment> commentsMongoWriter(MongoOperations mongoOperations) {
        return new MongoItemWriterBuilder<Comment>()
                .template(mongoOperations)
                .collection("comments")
                .build();
    }


    @Bean
    public Step convertGenres(ItemReader<GenreJpa> genreReader, ItemProcessor<GenreJpa, Genre> genreConverter, ItemWriter<Genre> genreWriter) {
        return stepBuilderFactory.get("convertGenresToMongo")
                .<GenreJpa, Genre>chunk(CHUNK_SIZE)
                .reader(genreReader)
                .processor(genreConverter)
                .writer(genreWriter)
                .build();
    }

    @Bean
    public Step convertAuthors(ItemReader<AuthorJpa> authorReader, ItemProcessor<AuthorJpa, Author> authorConverter, ItemWriter<Author> authorWriter) {
        return stepBuilderFactory.get("convertGenresToMongo")
                .<AuthorJpa, Author>chunk(CHUNK_SIZE)
                .reader(authorReader)
                .processor(authorConverter)
                .writer(authorWriter)
                .build();
    }

    @Bean
    public Step convertBooks(ItemReader<BookJpa> bookReader, ItemProcessor<BookJpa, Book> bookConverter, ItemWriter<Book> bookWriter) {
        return stepBuilderFactory.get("convertBooks")
                .<BookJpa, Book>chunk(CHUNK_SIZE)
                .reader(bookReader)
                .processor(bookConverter)
                .writer(bookWriter)
                .build();
    }

    @Bean
    public Step convertComments(ItemReader<CommentJpa> commentReader, ItemProcessor<CommentJpa, Comment> commentConverter, ItemWriter<Comment> commentWriter) {
        return stepBuilderFactory.get("convertComments")
                .<CommentJpa, Comment>chunk(CHUNK_SIZE)
                .reader(commentReader)
                .processor(commentConverter)
                .writer(commentWriter)
                .build();
    }

    @Bean
    public Job convertLibraryJob(Step convertGenres, Step convertAuthors, Step convertBooks, Step convertComments) {
        return jobBuilderFactory.get(CONVERT_LIBRARY_JOB_NAME)
                .start(convertGenres)
                .next(convertAuthors)
                .next(convertBooks)
                .next(convertComments)
                .build();
    }


}
