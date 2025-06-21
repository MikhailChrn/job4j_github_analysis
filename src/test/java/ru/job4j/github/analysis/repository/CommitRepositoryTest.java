package ru.job4j.github.analysis.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.github.analysis.entity.Commit;
import ru.job4j.github.analysis.entity.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommitRepositoryTest {

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    CommitRepository commitRepository;

    @BeforeEach
    public void deleteAllBefore() {
        commitRepository.deleteAll();
        repositoryRepository.deleteAll();
    }

    @AfterAll
    public void deleteAllAfterAll() {
        commitRepository.deleteAll();
        repositoryRepository.deleteAll();
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        Optional<Commit> optionalCommit = commitRepository.findById(1);

        assertThat(optionalCommit).isEmpty();
    }

    @Test
    public void whenSaveOneThenFindById() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

        Repository repository = Repository.builder()
                .title("title")
                .url("url").build();
        repositoryRepository.save(repository);

        Commit commit = Commit.builder()
                .message("message")
                .author("author")
                .date(now)
                .repository(repository).build();
        commitRepository.save(commit);

        Optional<Commit> foundedCommit = commitRepository.findById(commit.getId());

        assertThat(foundedCommit).isPresent();
        assertThat(foundedCommit.get().getAuthor()).isEqualTo(commit.getAuthor());
        assertThat(foundedCommit.get().getRepository())
                .isEqualTo(repository);
    }
}