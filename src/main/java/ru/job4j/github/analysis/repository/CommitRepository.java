package ru.job4j.github.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.github.analysis.entity.Commit;

public interface CommitRepository extends JpaRepository<Commit, Integer> {
}
