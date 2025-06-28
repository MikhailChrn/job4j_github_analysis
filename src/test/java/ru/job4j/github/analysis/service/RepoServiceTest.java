package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.github.analysis.comparator.CommitDateComparator;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;
import ru.job4j.github.analysis.repository.CommitRepository;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Mockito метод verifyNoInteractions() - гарантирует, что тестируемый код не взаимодействует
 *      с mockObject нежелательным образом.
 *  Mockito метод verify(mockObject, times(n)).someMethod() проверяет, что метод someMethod() был вызван ровно n раз
 *
 *  Использование when().thenReturn() для последовательных возвращаемых значений:
 *  when(myMock.someMethod()).thenReturn(1, 2, 3); // Первый вызов вернет 1, второй - 2, третий - 3
 */

@ExtendWith(MockitoExtension.class)
class RepoServiceTest {

    @Mock
    private CommitDateComparator commitDateComparator;

    @Mock
    private RepoRepository repoRepository;

    @Mock
    private CommitRepository commitRepository;

    @Mock
    private GitHubService gitHubService;

    @InjectMocks
    private RepoService repoService;

    @Test
    void whenCreateAndRepoAlreadyExistsThenReturns0() {
        String fullName = "octocat/Hello-World";

        when(repoRepository.findAllByFullName(fullName))
                .thenReturn(List.of(new RepoEntity()));

        int result = repoService.create(fullName);

        assertEquals(0, result);
        verifyNoInteractions(gitHubService);
    }

    @Test
    void whenCreateRepoNotExistsAndGitHubNotFoundThenReturnsMinus1() {
        String fullName = "octocat/Hello-World";

        when(repoRepository.findAllByFullName(fullName)).thenReturn(List.of());
        when(gitHubService.fetchRepo(fullName)).thenReturn(Optional.empty());

        int result = repoService.create(fullName);

        assertEquals(-1, result);
        verify(gitHubService).fetchRepo(fullName);
    }

    @Test
    void whenCreateRepoSavedAndCommitsSavedThenReturns1() {
        LocalDateTime now = LocalDateTime.now();

        String fullName = "octocat/Hello-World";
        RepoEntity repo = RepoEntity.builder().fullName(fullName).htmlUrl("repo0").build();
        CommitEntity commit0 = CommitEntity.builder().htmlUrl("commit0").authorDate(now.minusDays(2)).build();
        CommitEntity commit1 = CommitEntity.builder().htmlUrl("commit1").authorDate(now.minusDays(1)).build();

        when(repoRepository.findAllByFullName(fullName)).thenReturn(List.of(), List.of(repo));
        when(commitRepository.findAllByRepoFullName(fullName)).thenReturn(List.of(commit1, commit0));
        when(gitHubService.fetchRepo(fullName)).thenReturn(Optional.of(repo));
        when(gitHubService.fetchAllCommits(fullName))
                .thenReturn(List.of(commit0, commit1));
        when(commitDateComparator.compare(any(CommitEntity.class), any(CommitEntity.class)))
                .thenReturn(1);

        int result = repoService.create(fullName);

        assertEquals(1, result);
        verify(repoRepository, times(2)).save(repo);
    }

}