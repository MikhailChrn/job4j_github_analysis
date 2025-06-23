package ru.job4j.github.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.github.analysis.entity.RepoEntity;

import java.util.List;

public interface RepoRepository extends JpaRepository<RepoEntity, Integer> {

    List<RepoEntity> findAllByFullName(String fullName);

}
