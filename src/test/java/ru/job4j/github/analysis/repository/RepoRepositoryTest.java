package ru.job4j.github.analysis.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.github.analysis.entity.RepoEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepoRepositoryTest {

    @Autowired
    private RepoRepository repoRepository;

    @BeforeEach
    public void deleteAllBefore() {
        repoRepository.deleteAll();
    }

    @AfterAll
    public void deleteAllAfterAll() {
        repoRepository.deleteAll();
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        Optional<RepoEntity> optionalRepository = repoRepository.findById(1);

        assertThat(optionalRepository).isEmpty();
    }

    @Test
    public void whenSaveOneThenFindById() {
        LocalDateTime dateOfCreation = LocalDateTime.now(ZoneId.of("UTC")).minusYears(3);

        RepoEntity repo = RepoEntity.builder()
                .htmlUrl("https://github.com/Test/test_test")
                .fullName("Test/test_test")
                .createdAt(dateOfCreation).build();
        repoRepository.save(repo);

        Optional<RepoEntity> foundedRepository = repoRepository.findById(repo.getId());

        assertThat(foundedRepository).isPresent();
        assertThat(foundedRepository.get().getHtmlUrl()).isEqualTo(repo.getHtmlUrl());
    }

    @Test
    void whenSaveSeveralThenFindByFullName() {
        LocalDateTime dateOfCreation = LocalDateTime.now(ZoneId.of("UTC"));

        RepoEntity repo1 = RepoEntity.builder()
                .htmlUrl("https://github.com/Test1/test_test1")
                .fullName("Test1/test_test1")
                .createdAt(dateOfCreation).build();
        RepoEntity repo2 = RepoEntity.builder()
                .htmlUrl("https://github.com/Test2/test_test2")
                .fullName("Test2/test_test2")
                .createdAt(dateOfCreation).build();
        RepoEntity repo3 = RepoEntity.builder()
                .htmlUrl("https://github.com/Test3/test_test3")
                .fullName("Test3/test_test3")
                .createdAt(dateOfCreation).build();
        List.of(repo3, repo1, repo2).forEach(repoRepository::save);

        Optional<RepoEntity> foundedRepository =
                repoRepository.findAllByFullName(repo2.getFullName())
                        .stream().findFirst();

        assertThat(foundedRepository).isPresent();
        assertThat(foundedRepository.get().getHtmlUrl())
                .isEqualTo(repo2.getHtmlUrl());
    }

}