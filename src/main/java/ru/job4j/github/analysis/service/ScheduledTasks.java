package ru.job4j.github.analysis.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ScheduledTasks {
    private final RepositoryService repositoryService;
    private final GitHubRemote gitHubRemote;

    public void fetchCommits() {

    }
}
