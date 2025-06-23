package ru.job4j.github.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.github.analysis.entity.CommitEntity;

import java.util.List;

public interface CommitRepository extends JpaRepository<CommitEntity, Integer> {

    List<CommitEntity> findAllByRepoFullName(String fullName);

}
