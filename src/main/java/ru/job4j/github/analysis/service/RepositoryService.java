package ru.job4j.github.analysis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.github.analysis.dto.CommitDto;
import ru.job4j.github.analysis.dto.FullRepoNameDto;
import ru.job4j.github.analysis.dto.RepoDto;
import ru.job4j.github.analysis.mapper.CommitMapper;
import ru.job4j.github.analysis.mapper.RepoMapper;
import ru.job4j.github.analysis.repository.CommitRepository;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toCollection;

@Service
public class RepositoryService {

    @Autowired
    private RepoRepository repoRepository;

    @Autowired
    private RepoMapper repoMapper;

    @Autowired
    private CommitMapper commitMapper;

    @Autowired
    private CommitRepository commitRepository;

    /**
     * Метод запрашивает из базы все сохранённые репозитории
     */
    public List<RepoDto> findAllrepositories() {
        return repoRepository.findAll().stream()
                .map(repoMapper::getDtoFromEntity)
                .collect(toCollection(ArrayList<RepoDto>::new));
    }

    @Transactional
    public List<CommitDto> findAllCommitsByRepoTitle(String fullName) {
        return commitRepository.findAllByRepoFullName(fullName).stream()
                .map(commitMapper::getDtoFromEntity)
                .collect(toCollection(ArrayList<CommitDto>::new));
    }

    public void create(FullRepoNameDto fullRepoNameDto) {

    }
}
