package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledTasksIntegrationTest {

    @Mock
    private CommitService commitService;

    @Mock
    private RepoRepository repoRepository;

    @InjectMocks
    private ScheduledTasks scheduledTasks;

    @Test
    void testFetchTaskWithNewAndExistingCommits() {

        LocalDateTime now = LocalDateTime.now();

        RepoEntity repo = RepoEntity.builder().fullName("user/repo0").build();

        CommitEntity commit0 = CommitEntity.builder()
                .authorDate(now.minusMonths(3)).htmlUrl("http://github.com/user/repo0/commit/no1").repo(repo).build();
        CommitEntity commit1 = CommitEntity.builder()
                .authorDate(now.minusMonths(2)).htmlUrl("http://github.com/user/repo0/commit/no2").repo(repo).build();
        CommitEntity commit2 = CommitEntity.builder()
                .authorDate(now.minusMonths(6)).htmlUrl("http://github.com/user/repo0/commit/no3").repo(repo).build();
        CommitEntity commit3 = CommitEntity.builder()
                .authorDate(now.minusMonths(5)).htmlUrl("http://github.com/user/repo0/commit/no4").repo(repo).build();
        List<CommitEntity> recentCommitList = List.of(commit2, commit0, commit3, commit1);

        when(repoRepository.findAll()).thenReturn(List.of(repo));
        when(commitService.getRecentCommits(repo.getFullName())).thenReturn(recentCommitList);

        scheduledTasks.fetchTask();

        verify(repoRepository).findAll();
        verify(commitService).getRecentCommits(repo.getFullName());
    }
}