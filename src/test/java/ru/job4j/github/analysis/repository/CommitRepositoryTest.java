package ru.job4j.github.analysis.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommitRepositoryTest {

    @Autowired
    RepoRepository repoRepository;

    @Autowired
    CommitRepository commitRepository;

    @BeforeEach
    public void deleteAllBefore() {
        commitRepository.deleteAll();
        repoRepository.deleteAll();
    }

    @AfterAll
    public void deleteAllAfterAll() {
        commitRepository.deleteAll();
        repoRepository.deleteAll();
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        Optional<CommitEntity> optionalCommit = commitRepository.findById(1);

        assertThat(optionalCommit).isEmpty();
    }

    @Test
    public void whenSaveOneThenFindById() {
        LocalDateTime dateOfCreation = LocalDateTime.now(ZoneId.of("UTC")).minusYears(3);

        RepoEntity repo = RepoEntity.builder()
                .htmlUrl("https://github.com/Test/test_test")
                .fullName("Test/test_test")
                .createdAt(dateOfCreation).build();
        repoRepository.save(repo);

        CommitEntity commit = CommitEntity.builder()
                .htmlUrl("https://github.com/Test/test_test/commit/2ce0e62e9da8g3c1f0b3ce8fcd96r257c6dcb174")
                .message("message")
                .authorName("Test")
                .authorDate(dateOfCreation.plusMonths(1))
                .repo(repo).build();
        commitRepository.save(commit);

        Optional<CommitEntity> foundedCommit = commitRepository.findById(commit.getId());

        assertThat(foundedCommit).isPresent();
        assertThat(foundedCommit.get().getAuthorName()).isEqualTo(commit.getAuthorName());
        assertThat(foundedCommit.get().getRepo())
                .isEqualTo(repo);
    }

    @Test
    public void whenSaveSeveralThenFindByRepoId() {
        LocalDateTime dateOfCreation = LocalDateTime.now(ZoneId.of("UTC"));

        RepoEntity repo1 = RepoEntity.builder()
                .htmlUrl("https://github.com/Test1/test_test1")
                .fullName("Test1/test_test1").createdAt(dateOfCreation).build();
        RepoEntity repo2 = RepoEntity.builder()
                .htmlUrl("https://github.com/Test2/test_test2")
                .fullName("Test2/test_test2").createdAt(dateOfCreation).build();
        List.of(repo2, repo1).forEach(repoRepository::save);

        CommitEntity commit1 = CommitEntity.builder()
                .htmlUrl("https://github.com/Test1/test_test1/commit/cb171")
                .message("message1").authorName("Test1").authorDate(dateOfCreation.plusMonths(1))
                .repo(repo1).build();
        CommitEntity commit2 = CommitEntity.builder()
                .htmlUrl("https://github.com/Test2/test_test2/commit/cb272")
                .message("message2").authorName("Test2").authorDate(dateOfCreation.plusMonths(2))
                .repo(repo2).build();
        CommitEntity commit3 = CommitEntity.builder()
                .htmlUrl("https://github.com/Test1/test_test1/commit/cb173")
                .message("message4").authorName("Test1").authorDate(dateOfCreation.plusMonths(3))
                .repo(repo1).build();
        CommitEntity commit4 = CommitEntity.builder()
                .htmlUrl("https://github.com/Test2/test_test2/commit/cb274")
                .message("message4").authorName("Test2").authorDate(dateOfCreation.plusMonths(4))
                .repo(repo2).build();
        List.of(commit3, commit2, commit4, commit1).forEach(commitRepository::save);

        List<CommitEntity> foundedCommits = commitRepository.findAllByRepoFullName(repo2.getFullName());

        assertEquals(foundedCommits.size(), 2);
        assertTrue(foundedCommits
                .containsAll(List.of(commit4, commit2)));
    }
}