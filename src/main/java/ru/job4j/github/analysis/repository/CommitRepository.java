package ru.job4j.github.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.job4j.github.analysis.entity.CommitEntity;

import java.util.List;
import java.util.Optional;

public interface CommitRepository extends JpaRepository<CommitEntity, Integer> {

    List<CommitEntity> findAllByRepoFullName(String fullName);

    @Query(value = """
            SELECT EXISTS (
                SELECT *
                FROM commits c
                WHERE c.html_url = :htmlUrl
            )
            """, nativeQuery = true)
    boolean existsByUniqueHtmlUrl(@Param("htmlUrl") String htmlUrl);

    Optional<CommitEntity> findCommitEntityByHtmlUrl(String htmlUrl);

}
