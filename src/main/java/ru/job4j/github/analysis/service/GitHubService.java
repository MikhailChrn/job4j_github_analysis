package ru.job4j.github.analysis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.job4j.github.analysis.dto.response.CommitResponseDTO;
import ru.job4j.github.analysis.dto.response.RepoResponseDTO;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;

import ru.job4j.github.analysis.mapper.CommitMapper;
import ru.job4j.github.analysis.mapper.RepoMapper;
import ru.job4j.github.analysis.repository.CommitRepository;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Этот сервис выполняет выгрузку данных с публичного API GitHub.com
 * Формат названия репозитория : 'author/repository'
 * Пример названия репозитория : 'MikhailChrn/job4j_social_media_api'
 */
@Slf4j
@Service
public class GitHubService {

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private RepoRepository repoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RepoMapper repoMapper;

    @Autowired
    private CommitMapper commitMapper;

    /**
     * Метод выполняет выгрузку данных о репозитории по его названию
     */
    public Optional<RepoEntity> fetchRepo(String fullRepoName) {

        String urlRequest = String.format("https://api.github.com/repos/%s", fullRepoName);

        try {
            ResponseEntity<Optional<RepoResponseDTO>> response =
                    restTemplate.exchange(urlRequest, HttpMethod.GET, null,
                            new ParameterizedTypeReference<>() {
                            }
                    );

            Optional<RepoResponseDTO> optionalRepoResponseDto = response.getBody();

            if (optionalRepoResponseDto.isPresent()) {
                return Optional.of(
                        repoMapper.getEntityFromResponseDto(
                                optionalRepoResponseDto.get()));
            }
        } catch (HttpClientErrorException e) {
            log.warn(String.format("Репозиторий '%s' не найден", fullRepoName));
        }

        return Optional.empty();
    }

    /**
     * Метод выполняет выгрузку данных о коммитах по названию репозитория
     */
    public List<CommitEntity> fetchAllCommits(String fullRepoName) {

        String urlRequest = String.format("https://api.github.com/repos/%s/commits", fullRepoName);

        try {
            ResponseEntity<List<CommitResponseDTO>> response =
                    restTemplate.exchange(urlRequest, HttpMethod.GET, null,
                            new ParameterizedTypeReference<>() {
                            }
                    );

            List<CommitResponseDTO> commitResponseDTOList = response.getBody();

            if (!commitResponseDTOList.isEmpty()) {
                return commitResponseDTOList.stream()
                        .map(commitMapper::getEntityFromResponseDto)
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        } catch (HttpClientErrorException e) {
            log.warn(String.format("Коммиты в репозитории '%s' не найдены", fullRepoName));
        }

        return List.of();
    }

    /**
     * Метод выполняет выгрузку данных о коммитах по названию репозитория,
     * сравнивает с тем что уже есть в базе и возвращеет разницу
     */
    public List<CommitEntity> getRecentCommits(String fullRepoName) {
        Optional<RepoEntity> optionalRepoEntity =
                repoRepository.findAllByFullName(fullRepoName).stream().findFirst();
        if (optionalRepoEntity.isEmpty()) {
            return List.of();
        }

        List<CommitEntity> allCommitsByRepoExternal = fetchAllCommits(fullRepoName);
        List<CommitEntity> allCommitsByRepoInternal =
                commitRepository.findAllByRepoFullName(fullRepoName);

        List<CommitEntity> recentCommits = allCommitsByRepoExternal.stream()
                .filter(commit -> !allCommitsByRepoInternal.contains(commit))
                .toList();

        return recentCommits;
    }
}