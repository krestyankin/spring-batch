package ru.krestyankin.library.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authors")
public class AuthorJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fullname", nullable = false, unique = false)
    private String fullname;
    @Column(name = "dob", nullable = true, unique = false)
    private Date dateOfBirth;

    @ManyToMany(mappedBy = "authors")
    private List<BookJpa> books;

    @Override
    public String toString() {
        return "AuthorJpa{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
