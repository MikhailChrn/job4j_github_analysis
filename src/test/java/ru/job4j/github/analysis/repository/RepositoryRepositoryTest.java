package ru.job4j.github.analysis.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.github.analysis.entity.Repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepositoryRepositoryTest {

    @Autowired
    private RepositoryRepository repositoryRepository;

    @BeforeEach
    public void deleteAllBefore() {
        repositoryRepository.deleteAll();
    }

    @AfterAll
    public void deleteAllAfterAll() {
        repositoryRepository.deleteAll();
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        Optional<Repository> optionalRepository = repositoryRepository.findById(1);

        assertThat(optionalRepository).isEmpty();
    }

    @Test
    public void whenSaveOneThenFindById() {
        Repository repository = Repository.builder()
                .title("title")
                .url("url").build();
        repositoryRepository.save(repository);

        Optional<Repository> foundedRepository = repositoryRepository.findById(repository.getId());

        assertThat(foundedRepository).isPresent();
        assertThat(foundedRepository.get().getUrl()).isEqualTo(repository.getUrl());
    }

}