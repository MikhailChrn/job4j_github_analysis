package ru.job4j.github.analysis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.repository.CommitRepository;

import java.util.List;

/**
 *
 */

@Service
public class CommitService {

    @Autowired
    private CommitRepository commitRepository;

    @Transactional
    public boolean save(CommitEntity commitEntity) {
        if (commitRepository.existsByUniqueHtmlUrl(commitEntity.getHtmlUrl())) {
            return false;
        }
        commitRepository.save(commitEntity);

        return true;
    }

    public List<CommitEntity> findAllByRepoFullName(String repoFullName) {
        return commitRepository.findAllByRepoFullName(repoFullName);
    }
}
