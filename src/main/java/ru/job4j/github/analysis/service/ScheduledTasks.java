package ru.job4j.github.analysis.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Этот сервис должен по расписанию подгружать изменения из сохраненных ранее репозиториев.
 * То есть этот сервис должен использовать аннотацию @Scheduled.
 */

@AllArgsConstructor
@Service
public class ScheduledTasks {
    private final RepositoryService repositoryService;
    private final GitHubRemote gitHubRemote;

    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void fetchCommits() {
        // Логика для подгрузки коммитов из репозиториев
    }
}
