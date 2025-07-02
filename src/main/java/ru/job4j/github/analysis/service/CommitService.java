package ru.job4j.github.analysis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.github.analysis.dto.request.CommitRequestDTO;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;
import ru.job4j.github.analysis.mapper.CommitMapper;
import ru.job4j.github.analysis.repository.CommitRepository;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toCollection;

@Slf4j
@Service
public class CommitService {

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private CommitMapper commitMapper;

    @Autowired
    private RepoRepository repoRepository;

    @Autowired
    private GitHubService gitHubService;

    /**
     * Метод проверяет наличие коммита в базе и возвращает 'false',
     * если с данным 'url' коммит уже сохранён
     * ИНАЧЕ сохраняет в базу данный коммит
     */
    @Transactional
    public boolean save(CommitEntity commitEntity) {
        if (commitRepository.existsByUniqueHtmlUrl(commitEntity.getHtmlUrl())) {
            return false;
        }
        commitRepository.save(commitEntity);

        return true;
    }

    /**
     * Метод запрашивает из базы все сохранённые коммиты по имени репозитория
     */
    public List<CommitRequestDTO> findAllByRepoFullName(String repoFullName) {
        List<CommitRequestDTO> request = commitRepository.findAllByRepoFullName(repoFullName).stream()
                .map(commitMapper::getRequestDtoFromEntity)
                .collect(toCollection(ArrayList<CommitRequestDTO>::new));

        log.info(String.format("Возвращены данные о '%d шт.' коммитах. Время : %s.",
                request.size(), LocalDateTime.now()));

        return request;
    }


    /**
     * Метод выполняет выгрузку данных о коммитах по названию репозитория,
     * сравнивает с тем что уже есть в базе и возвращает разницу
     */
    @Transactional
    public List<CommitEntity> getRecentCommits(String fullRepoName) {
        Optional<RepoEntity> optionalRepoEntity =
                repoRepository.findAllByFullName(fullRepoName).stream().findFirst();
        if (optionalRepoEntity.isEmpty()) {
            return List.of();
        }

        List<CommitEntity> allCommitsByRepoExternal = gitHubService.fetchAllCommits(fullRepoName);
        List<CommitEntity> allCommitsByRepoInternal =
                commitRepository.findAllByRepoFullName(fullRepoName);

        List<CommitEntity> recentCommits = allCommitsByRepoExternal.stream()
                .filter(commit -> !allCommitsByRepoInternal.contains(commit))
                .toList();

        log.info(String.format("Возвращены данные о '%d шт.' коммитах, отсутствующих в локальной БД. Время : %s.",
                recentCommits.size(), LocalDateTime.now()));

        return recentCommits;
    }
}
