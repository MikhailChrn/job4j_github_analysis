package ru.job4j.github.analysis.mapper;

import org.springframework.stereotype.Component;
import ru.job4j.github.analysis.dto.CommitDto;
import ru.job4j.github.analysis.entity.CommitEntity;

@Component
public class CommitMapper {

    public CommitDto getDtoFromEntity(CommitEntity commitEntity) {
        return CommitDto.builder()
                .fullNameRepo(commitEntity.getRepo().getFullName())
                .htmlUrl(commitEntity.getHtmlUrl())
                .message(commitEntity.getMessage())
                .authorName(commitEntity.getAuthorName())
                .authorDate(commitEntity.getAuthorDate()).build();
    }
}
