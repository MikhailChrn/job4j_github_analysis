package ru.job4j.github.analysis.service;

import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.entity.Commit;
import ru.job4j.github.analysis.entity.Repository;

import java.util.List;

@Service
public class GitHubService {

    public List<Repository> fetchRepositories(String username) {
        return List.of();
    }

    public List<Commit> fetchCommits(String repository) {
        return List.of();
    }
}