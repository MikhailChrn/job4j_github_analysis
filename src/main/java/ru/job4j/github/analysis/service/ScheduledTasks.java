package ru.job4j.github.analysis.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.entity.CommitEntity;

import java.util.List;

/**
 * Этот сервис по расписанию подгружает изменения из сохраненных репозиториев.
 */

@Service
public class ScheduledTasks {

    @Autowired
    private RepoService repoService;

    @Autowired
    private GitHubService gitHubService;

    /*@Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void fetchCommits() throws DataAccessException {
        List<CommitEntity> recentCommits = commitService.findAllRecentCommits();
        for (CommitEntity recentCommit : recentCommits) {
            List<CommitEntity> commitsFromGit = gitHubService
                    .fetchCommits(recentCommit.getAuthor(), recentCommit.getRepository().getName());
            List<CommitEntity> newCommits = findNewCommits(commitsFromGit, recentCommit.getSha());
            newCommits.forEach(commit -> commit.setRepository(recentCommit.getRepository()));
            commitService.saveAll(newCommits);
        }
    }

    private List<Commit> findNewCommits(List<Commit> commitsFromGit, String currentSha) {
        return commitsFromGit.stream()
                .takeWhile(commitFromGit -> !commitFromGit.getSha().equals(currentSha))
                .toList();
    }*/
}
