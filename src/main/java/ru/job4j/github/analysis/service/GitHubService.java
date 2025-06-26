package ru.job4j.github.analysis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.job4j.github.analysis.dto.FullRepoNameDTO;
import ru.job4j.github.analysis.dto.response.CommitResponseDTO;
import ru.job4j.github.analysis.dto.response.RepoResponseDTO;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;

import ru.job4j.github.analysis.mapper.CommitMapper;
import ru.job4j.github.analysis.mapper.RepoMapper;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Этот сервис выполняет подгрузку данных с публичного API
 */

@Slf4j
@Service
public class GitHubService {

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
    public Optional<RepoEntity> fetchRepo(FullRepoNameDTO fullRepoNameDto) {

        String url = "https://api.github.com/repos/" + fullRepoNameDto.getFullName();

        try {
            ResponseEntity<Optional<RepoResponseDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
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
            log.warn("Репозиторий '{}' не найден.", fullRepoNameDto);
        }
        return Optional.empty();
    }

    /**
     * Метод выполняет выгрузку данных о коммитах по названию репозитория
     */
    public List<CommitEntity> fetchAllCommits(FullRepoNameDTO fullRepoNameDto) {

        String url = "https://api.github.com/repos/" + fullRepoNameDto.getFullName() + "/commits";

        try {
            ResponseEntity<List<CommitResponseDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
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
            log.warn("Коммиты в репозитории '{}' не найдены.", fullRepoNameDto);
        }
        return List.of();
    }

    /**
     * Метод выполняет выгрузку данных о коммитах по названию репозитория
     */
    public List<CommitEntity> fetchCommitsLatestThan(FullRepoNameDTO fullRepoNameDto, String commitHtmlUrl) {

        String url = String.format("https://api.github.com/repos/%s/commits?sha=%s",
                fullRepoNameDto, getShaFromUrl(commitHtmlUrl));

        try {
            ResponseEntity<List<CommitResponseDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
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
            log.warn("Коммиты в репозитории '{}' не найдены.", fullRepoNameDto);
        }
        return List.of();
    }

    private String getShaFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}