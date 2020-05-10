package ru.krestyankin.library.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class BookJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @BatchSize(size = 10)
    @ManyToMany(targetEntity = AuthorJpa.class, fetch = FetchType.EAGER)
    @JoinTable(name = "books_authors",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id"))
    private Set<AuthorJpa> authors;

    @BatchSize(size = 10)
    @ManyToMany(targetEntity = GenreJpa.class, fetch = FetchType.EAGER)
    @JoinTable(name = "books_genres",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id", referencedColumnName = "id"))
    private Set<GenreJpa> genres;

    @BatchSize(size = 10)
    @OneToMany(targetEntity = CommentJpa.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="book_id")
    private List<CommentJpa> comments;

    @Override
    public String toString() {
        return "BookJpa{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", genres=" + genres +
                '}';
    }
}
