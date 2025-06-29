package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;
import ru.job4j.github.analysis.repository.CommitRepository;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledTasksIntegrationTest {

    @Mock
    RepoRepository repoRepository;

    @Mock
    CommitRepository commitRepository;

    @Mock
    private GitHubService gitHubService;

    @Mock
    private RepoService repoService;

    @InjectMocks
    @Autowired
    private ScheduledTasks scheduledTasks;

    @Test
    void testFetchTaskWithNewAndExistingCommits() {
        LocalDateTime now = LocalDateTime.now();
        RepoEntity repo0 = RepoEntity.builder().lastCommitUrl("").fullName("user/repo0").build();
        RepoEntity repo1 = RepoEntity.builder().lastCommitUrl("http://github.com/user/repo1/commit/no1")
                .fullName("user/repo1").build();
        List<RepoEntity> repoList = List.of(repo1, repo0);

        CommitEntity commit01 = CommitEntity.builder()
                .authorDate(now.minusMonths(3)).htmlUrl("http://github.com/user/repo0/commit/no1").build();
        CommitEntity commit02 = CommitEntity.builder()
                .authorDate(now.minusMonths(2)).htmlUrl("http://github.com/user/repo0/commit/no2").build();
        CommitEntity commit11 = CommitEntity.builder()
                .authorDate(now.minusMonths(6)).htmlUrl("http://github.com/user/repo1/commit/no1").repo(repo1).build();
        CommitEntity commit12 = CommitEntity.builder()
                .authorDate(now.minusMonths(5)).htmlUrl("http://github.com/user/repo1/commit/no2").build();

        when(repoRepository.findAll()).thenReturn(repoList);
        when(gitHubService.fetchAllCommits("user/repo0"))
                .thenReturn(List.of(commit02, commit01));
        when(gitHubService.fetchCommitsLatestThan("user/repo1", commit11.getHtmlUrl()))
                .thenReturn(List.of(commit12));

        scheduledTasks.fetchTask();

        verify(gitHubService).fetchAllCommits("user/repo0");
        verify(gitHubService).fetchCommitsLatestThan("user/repo1", commit11.getHtmlUrl());
    }
}