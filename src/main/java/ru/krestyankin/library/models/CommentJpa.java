package ru.krestyankin.library.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class CommentJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "text", nullable = false, unique = false)
    private String text;

    @OneToOne(targetEntity = BookJpa.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "book_id")
    private BookJpa book;

    @Override
    public String toString() {
        return "CommentJpa{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
