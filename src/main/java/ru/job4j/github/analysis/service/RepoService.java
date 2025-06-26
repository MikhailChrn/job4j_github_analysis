package ru.job4j.github.analysis.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.github.analysis.comparator.CommitDateComparator;
import ru.job4j.github.analysis.dto.request.CommitRequestDTO;
import ru.job4j.github.analysis.dto.FullRepoNameDTO;
import ru.job4j.github.analysis.dto.request.RepoRequestDTO;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;
import ru.job4j.github.analysis.mapper.CommitMapper;
import ru.job4j.github.analysis.mapper.RepoMapper;
import ru.job4j.github.analysis.repository.CommitRepository;
import ru.job4j.github.analysis.repository.RepoRepository;

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
        return repoRepository.findAll().stream()
                .map(repoMapper::getRequestDtoFromEntity)
                .collect(toCollection(ArrayList<RepoRequestDTO>::new));
    }

    /**
     * Метод запрашивает из базы все сохранённые коммиты по имени репозитория
     */
    @Transactional
    public List<CommitRequestDTO> findAllCommitsByRepoFullName(String fullName) {
        return commitRepository.findAllByRepoFullName(fullName).stream()
                .map(commitMapper::getRequestDtoFromEntity)
                .collect(toCollection(ArrayList<CommitRequestDTO>::new));
    }

    /**
     * Метод добавляет в базу репозиторий по заголовку,
     * для дальнейшего выполнения мониторинга коммитов из него
     */
    public void create(FullRepoNameDTO fullRepoNameDto) throws EntityNotFoundException {
        Optional<RepoEntity> optionalRepoEntity = gitHubService.fetchRepo(fullRepoNameDto);
        if (optionalRepoEntity.isEmpty()) {
            throw new EntityNotFoundException(
                    String.format("Данные о репозитории '%s' отсутствуют в базе",
                            fullRepoNameDto.getFullName()));
        }
        repoRepository.save(optionalRepoEntity.get());
    }

    /**
     * Метод находит html_url крайнего коммита
     * и обновляет о нём данные в соответствующем поле repoEntity
     */
    @Transactional
    public Optional<CommitEntity> findLastCommitByRepoFullName(String fullName) {
        Optional<RepoEntity> optionalRepoEntity = repoRepository.findAllByFullName(fullName)
                .stream().findFirst();

        if (optionalRepoEntity.isEmpty()) {
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
                .max(commitDateComparator::compare)
                .get();

        optionalRepoEntity.get().setLastCommitUrl(lastCommit.getHtmlUrl());

        log.info(String.format("'%s' коммит сохранён в базу как наиболее свежий в настоящий момент", lastCommit.getHtmlUrl()));

        repoRepository.save(optionalRepoEntity.get());

        return Optional.of(lastCommit);
    }

}
