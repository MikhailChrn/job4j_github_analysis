package ru.job4j.github.analysis.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.entity.RepoEntity;
import ru.job4j.github.analysis.repository.CommitRepository;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Этот сервис по расписанию подгружает изменения из сохраненных репозиториев.
 */
@Slf4j
@Service
public class ScheduledTasks {

    @Autowired
    private RepoService repoService;

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private RepoRepository repoRepository;

    @Autowired
    private CommitRepository commitRepository;

    /**
     * ЕСЛИ в базе по репозиторию отсутствуют коммиты -> выгружаем все имеющиеся из GitHub
     * ИНАЧЕ выгружаем из GitHub только те, что после крайнего
     */
    @Async
    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void fetchCommits() {
        List<RepoEntity> allRepoList = repoRepository.findAll();

        log.info(String.format("Мониторинг для '%d шт.' репозиториев. Время запуска : %s. Поток : %s",
                allRepoList.size(), LocalDateTime.now(), Thread.currentThread().getName()));

        allRepoList.forEach(repo -> {
            if (repo.getLastCommitUrl().isEmpty()) {
                gitHubService.fetchAllCommits(repo.getFullName())
                        .forEach(commitRepository::save);
            } else {
                gitHubService.fetchCommitsLatestThan(repo.getFullName(), repo.getLastCommitUrl())
                        .forEach(commitRepository::save);
            }
        });

        log.info(String.format("Завершён мониторинг для '%d шт.' репозиториев. Время окончания : %s.",
                allRepoList.size(), LocalDateTime.now(), Thread.currentThread().getName()));

    }
}
