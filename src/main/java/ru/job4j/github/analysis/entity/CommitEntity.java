package ru.job4j.github.analysis.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"htmlUrl"})
@Table (name = "commits")
public class CommitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @URL
    @Column(name = "html_url", unique = true)
    private String htmlUrl;

    @Column(name = "message")
    private String message;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "author_date")
    private LocalDateTime authorDate;

    @ManyToOne
    @JoinColumn(name = "repo_id")
    private RepoEntity repo;
}
