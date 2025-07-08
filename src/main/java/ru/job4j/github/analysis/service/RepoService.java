package ru.job4j.github.analysis.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.github.analysis.comparator.CommitDateComparator;
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
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toCollection;

@Slf4j
@Service
public class RepoService {

    @Autowired
    private CommitService commitService;

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
    public List<RepoRequestDTO> findAllRepo() {
        List<RepoRequestDTO> request = repoRepository.findAll().stream()
                .map(repoMapper::getRequestDtoFromEntity)
                .collect(toCollection(ArrayList<RepoRequestDTO>::new));

        log.info(String.format("Возвращены данные о '%d шт.' репозиториев. Время : %s.",
                request.size(), LocalDateTime.now()));

        return request;
    }

    /**
     * Метод добавляет в базу репозиторий по заголовку для дальнейшего выполнения мониторинга
     */
    @Transactional
    public RepoServiceStatus create(String fullRepoName) {
        List<RepoEntity> repoList = repoRepository.findAllByFullName(fullRepoName);
        if (!repoList.isEmpty()) {
            return RepoServiceStatus.IS_ADD_EARLIER;
        }

        Optional<RepoEntity> optionalRepoEntity = gitHubService.fetchRepo(fullRepoName);
        if (optionalRepoEntity.isEmpty()) {
            return RepoServiceStatus.IST_FOUND_ON_EXTERNAL;
        }

        optionalRepoEntity.get().setNew(true);
        repoRepository.save(optionalRepoEntity.get());
        log.info(String.format("Репозиторий '%s' успешно добавлен базу данных", fullRepoName));

        return RepoServiceStatus.SUCCESSFULLY_SAVED;
    }
}
