package ru.job4j.github.analysis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private CommitService commitService;

    @Autowired
    private RepoService repoService;

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private RepoRepository repoRepository;

    @Autowired
    private CommitRepository commitRepository;

    @Value("${scheduler.fixedRate}")
    private long rate;

    /**
     * ЕСЛИ флаг 'isNew'у репозитория поднят -> выгружаем все имеющиеся коммиты из GitHub
     * ИНАЧЕ выгружаем из GitHub только те, что после крайнего
     */
    @Async
    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void fetchTask() {
        log.info(String.format("Выполняется обновления информации о коммитах. Интервал выполнения %d сек",
                rate / 1000));

        List<RepoEntity> allRepoList = repoRepository.findAll();

        log.info(String.format("Мониторинг для '%d шт.' репозиториев. Время запуска : %s. Поток : %s",
                allRepoList.size(), LocalDateTime.now(), Thread.currentThread().getName()));

        allRepoList.forEach(repo -> {
            if (!repo.isNew()) {
                commitService.getRecentCommits(repo.getFullName()).forEach(commitService::save);
            } else {
                gitHubService.fetchAllCommits(repo.getFullName()).forEach(commitService::save);
                repo.setNew(false);
                repoRepository.save(repo);
            }
        });

        log.info(String.format("Завершён мониторинг для '%d шт.' репозиториев. Время окончания : %s.",
                allRepoList.size(), LocalDateTime.now(), Thread.currentThread().getName()));
    }
}
