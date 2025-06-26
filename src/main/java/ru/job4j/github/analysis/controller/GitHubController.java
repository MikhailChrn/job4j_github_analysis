package ru.job4j.github.analysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.job4j.github.analysis.dto.request.CommitRequestDTO;
import ru.job4j.github.analysis.dto.FullRepoNameDTO;
import ru.job4j.github.analysis.dto.request.RepoRequestDTO;
import ru.job4j.github.analysis.service.RepoService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GitHubController {

    @Autowired
    private RepoService repoService;

    /**
     * GET возвращает перечень локально сохранённых репозиториев
     */
    @GetMapping("/repositories")
    public List<RepoRequestDTO> getAllRepositories() {
        return repoService.findAllrepositories();
    }

    /**
     * GET возвращает перечень локально сохранённых коммитов из запрашиваемого репозитория
     */
    @GetMapping("/commits/{repoTitle}")
    public List<CommitRequestDTO> getCommitsByRepoTitle(@PathVariable(value = "repoTitle") String repoTitle) {
        return repoService.findAllCommitsByRepoFullName(repoTitle);
    }

    /**
     * POST добавляет репозиторий в БД для дальнейшего сбора данных о коммитах
     */
    @PostMapping("/repository")
    public ResponseEntity<Void> create(@RequestBody FullRepoNameDTO fullRepoNameDto) throws Exception {
        repoService.create(fullRepoNameDto);
        return ResponseEntity.noContent().build();
    }
}
