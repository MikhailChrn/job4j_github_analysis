package ru.job4j.github.analysis.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "repositories")
public class Repository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
}
