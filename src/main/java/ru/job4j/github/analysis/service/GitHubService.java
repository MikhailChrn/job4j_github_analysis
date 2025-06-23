package ru.job4j.github.analysis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.job4j.github.analysis.dto.FullRepoNameDto;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;

import java.util.List;

@Service
public class GitHubService {

    @Autowired
    private RestTemplate restTemplate;

    public List<RepoEntity> fetchRepositories(String username) {
        String url = "https://api.github.com/users/" + username + "/repos";
        ResponseEntity<List<RepoEntity>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<RepoEntity>>() {
                });
        return response.getBody();
    }

    public List<CommitEntity> fetchCommits(FullRepoNameDto fullRepoNameDto) {
        String url = "https://api.github.com/repos/" + fullRepoNameDto.getFullName() + "/commits";
        ResponseEntity<List<CommitEntity>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CommitEntity>>() {
                });
        return response.getBody();
    }

}