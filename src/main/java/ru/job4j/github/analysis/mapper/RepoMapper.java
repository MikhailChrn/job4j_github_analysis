package ru.job4j.github.analysis.mapper;

import org.springframework.stereotype.Component;
import ru.job4j.github.analysis.entity.RepoEntity;
import ru.job4j.github.analysis.dto.RepoDto;

@Component
public class RepoMapper {

    public RepoDto getDtoFromEntity(RepoEntity repoEntity) {
        return RepoDto.builder()
                .htmlUrl(repoEntity.getHtmlUrl())
                .fullName(repoEntity.getFullName())
                .createdAt(repoEntity.getCreatedAt()).build();
    }
}
