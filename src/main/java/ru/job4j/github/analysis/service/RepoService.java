package ru.job4j.github.analysis.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.github.analysis.comparator.CommitDateComparator;
import ru.job4j.github.analysis.dto.request.CommitRequestDTO;
import ru.job4j.github.analysis.dto.request.RepoRequestDTO;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;
import ru.job4j.github.analysis.mapper.CommitMapper;
import ru.job4j.github.analysis.mapper.RepoMapper;
import ru.job4j.github.analysis.repository.CommitRepository;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toCollection;

@Slf4j
@Service
public class RepoService {

    @Autowired
    private RepoRepository repoRepository;

    @Autowired
    private RepoMapper repoMapper;

    @Autowired
    private CommitMapper commitMapper;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private CommitDateComparator commitDateComparator;

    /**
     * Метод запрашивает из базы все сохранённые репозитории
     */
    public List<RepoRequestDTO> findAllrepositories() {
        List<RepoRequestDTO> request = repoRepository.findAll().stream()
                .map(repoMapper::getRequestDtoFromEntity)
                .collect(toCollection(ArrayList<RepoRequestDTO>::new));

        log.info(String.format("Возвращены данные о '%d шт.' репозиториев. Время : %s.",
                request.size(), LocalDateTime.now()));

        return request;
    }

    /**
     * Метод запрашивает из базы все сохранённые коммиты по имени репозитория
     */
    public List<CommitRequestDTO> findAllCommitsByRepoFullName(String fullName) {
        List<CommitRequestDTO> request = commitRepository.findAllByRepoFullName(fullName).stream()
                .map(commitMapper::getRequestDtoFromEntity)
                .collect(toCollection(ArrayList<CommitRequestDTO>::new));

        log.info(String.format("Возвращены данные о '%d шт.' коммитах. Время : %s.",
                request.size(), LocalDateTime.now()));

        return request;
    }

    /**
     * Метод добавляет в базу репозиторий по заголовку для дальнейшего выполнения мониторинга
     * (+ все имеющиеся к настоящему моменту коммиnы
     *  + фиксирует данные о последнем имеющемся коммите)
     *
     *   0 -> репозиторий добавлен ранее
     *  -1 -> репозиторий не найден на API GitHub.com
     *  +1 -> репозиторий и все коммиты успешно сохранены в базе
     */
    @Async
    @Transactional
    public int create(String fullRepoName) throws EntityNotFoundException {
        List<RepoEntity> repoList = repoRepository.findAllByFullName(fullRepoName);
        if (!repoList.isEmpty()) {
            return 0;
        }

        Optional<RepoEntity> optionalRepoEntity = gitHubService.fetchRepo(fullRepoName);
        if (optionalRepoEntity.isEmpty()) {
            return -1;
        }

        repoRepository.save(optionalRepoEntity.get());
        log.info(String.format("Репозиторий %s добавлен базу данных", fullRepoName));

        List<CommitEntity> commitEntityList = gitHubService.fetchAllCommits(fullRepoName);

        commitEntityList.forEach(commitRepository::save);
        log.info(String.format("'%d шт.' коммитов добавлено в базу. Время : %s",
                commitEntityList.size(), LocalDateTime.now()));

        findLastCommitByRepoFullName(fullRepoName);
        return 1;
    }

    /**
     * Метод находит html_url крайнего коммита
     * и обновляет о нём данные в соответствующем поле repoEntity
     */
    @Transactional
    public Optional<CommitEntity> findLastCommitByRepoFullName(String fullName) {
        List<RepoEntity> repoEntityList = repoRepository.findAllByFullName(fullName);

        if (repoEntityList.isEmpty()) {
            log.warn(String.format("Данные о репозитории '%s' отсутствуют в базе", fullName));
            throw new EntityNotFoundException(
                    String.format("Данные о репозитории '%s' отсутствуют в базе", fullName));
        }

        List<CommitEntity> commitEntityList = commitRepository.findAllByRepoFullName(fullName);
        if (commitEntityList.isEmpty()) {
            log.warn(String.format("Данные о коммитах '%s' отсутствуют в базе", fullName));
            throw new EntityNotFoundException(
                    String.format("Данные о коммитах '%s' отсутствуют в базе", fullName));
        }

        CommitEntity lastCommit = commitEntityList.stream()
                .max(commitDateComparator)
                .get();

        repoEntityList.stream().findFirst().get().setLastCommitUrl(lastCommit.getHtmlUrl());

        repoRepository.save(repoEntityList.stream().findFirst().get());
        log.info(String.format("'%s' коммит сохранён в базу как крайний", lastCommit.getHtmlUrl()));

        return Optional.of(lastCommit);
    }
}
