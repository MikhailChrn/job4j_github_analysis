package ru.job4j.github.analysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.job4j.github.analysis.dto.FullRepoNameDTO;
import ru.job4j.github.analysis.dto.request.CommitRequestDTO;
import ru.job4j.github.analysis.dto.request.RepoRequestDTO;
import ru.job4j.github.analysis.service.RepoService;

import java.util.List;

/**
 * Это единственный REST контроллер  API GitHub.com
 * Формат названия репозитория : 'author/repository'
 * Пример названия репозитория : 'MikhailChrn/job4j_social_media_api'
 */
@RestController
@RequestMapping("/api")
public class GitHubController {

    @Autowired
    private RepoService repoService;

    /**
     * GET возвращает перечень локально сохранённых репозиториев
     */
    @GetMapping("/repos")
    @ResponseStatus(HttpStatus.OK)
    public List<RepoRequestDTO> getAllRepositories() {

        return repoService.findAllrepositories();
    }

    /**
     * GET возвращает перечень локально сохранённых в БД коммитов
     * из запрашиваемого репозитория
     */
    @GetMapping("/commits")
    @ResponseStatus(HttpStatus.OK)
    public List<CommitRequestDTO> getCommitsByRepoFullName(@RequestBody FullRepoNameDTO fullRepoNameDTO) {

        return repoService.findAllCommitsByRepoFullName(fullRepoNameDTO.getFullName());
    }

    /**
     * POST сохраняет репозиторий в БД для последующего сбора данных о коммитах
     */
    @PostMapping("/repos")
    public ResponseEntity<String> create(@RequestBody FullRepoNameDTO fullRepoNameDTO) throws Exception {
        int response = repoService.create(fullRepoNameDTO.getFullName()).get();

        if (response == 0) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                    String.format("Репозиторий '%s' добавлен ранее", fullRepoNameDTO.getFullName()));
        } else if (response == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    String.format("Репозиторий '%s' не найден на внешнем ресурсе", fullRepoNameDTO.getFullName()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                String.format("Репозиторий %s успешно добавлен", fullRepoNameDTO.getFullName()));
    }
}
