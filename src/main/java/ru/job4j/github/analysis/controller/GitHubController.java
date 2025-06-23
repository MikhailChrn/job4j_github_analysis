package ru.job4j.github.analysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.job4j.github.analysis.dto.CommitDto;
import ru.job4j.github.analysis.dto.FullRepoNameDto;
import ru.job4j.github.analysis.dto.RepoDto;
import ru.job4j.github.analysis.service.RepositoryService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GitHubController {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * GET возвращает перечень локально сохранённых репозиториев
     */
    @GetMapping("/repositories")
    public List<RepoDto> getAllRepositories() {
        return repositoryService.findAllrepositories();
    }

    /**
     * GET возвращает перечень локально сохранённых коммитов из запрашиваемого репозитория
     */
    @GetMapping("/commits/{repoTitle}")
    public List<CommitDto> getCommitsByRepoTitle(@PathVariable(value = "repoTitle") String repoTitle) {
        return repositoryService.findAllCommitsByRepoTitle(repoTitle);
    }

    /**
     * POST добавляет репозиторий в БД для дальнейшего сбора данных о коммитах
     */
    @PostMapping("/repository")
    public ResponseEntity<Void> create(@RequestBody FullRepoNameDto fullRepoNameDto) {
        repositoryService.create(fullRepoNameDto);
        return ResponseEntity.noContent().build();
    }
}
