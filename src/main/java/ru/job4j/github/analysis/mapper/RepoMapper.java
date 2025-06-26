package ru.job4j.github.analysis.mapper;

import org.springframework.stereotype.Component;
import ru.job4j.github.analysis.dto.response.RepoResponseDTO;
import ru.job4j.github.analysis.entity.RepoEntity;
import ru.job4j.github.analysis.dto.request.RepoRequestDTO;

@Component
public class RepoMapper {

    public RepoRequestDTO getRequestDtoFromEntity(RepoEntity entity) {
        return RepoRequestDTO.builder()
                .htmlUrl(entity.getHtmlUrl())
                .fullName(entity.getFullName())
                .createdAt(entity.getCreatedAt())
                .description(entity.getDescription()).build();
    }

    public RepoEntity getEntityFromResponseDto(RepoResponseDTO dto) {
        return RepoEntity.builder()
                .htmlUrl(dto.getHtmlUrl())
                .fullName(dto.getFullName())
                .createdAt(dto.getCreatedAt())
                .description(dto.getDescription()).build();
    }
}
