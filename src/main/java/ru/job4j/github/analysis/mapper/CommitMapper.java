package ru.job4j.github.analysis.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.job4j.github.analysis.dto.request.CommitRequestDTO;
import ru.job4j.github.analysis.dto.response.CommitResponseDTO;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;
import ru.job4j.github.analysis.repository.RepoRepository;

@Component
public class CommitMapper {

    @Autowired
    private RepoRepository repoRepository;

    public CommitRequestDTO getRequestDtoFromEntity(CommitEntity entity) {
        return CommitRequestDTO.builder()
                .fullNameRepo(entity.getRepo().getFullName())
                .htmlUrl(entity.getHtmlUrl())
                .message(entity.getMessage())
                .authorName(entity.getAuthorName())
                .authorDate(entity.getAuthorDate()).build();
    }

    public CommitEntity getEntityFromResponseDto(CommitResponseDTO dto) {
        RepoEntity repo = repoRepository.findAllByFullName(
                        getFullNameFromHtmlUrl(dto.getHtmlUrl())).stream()
                .findFirst().get();

        return CommitEntity.builder()
                .htmlUrl(dto.getHtmlUrl())
                .message(dto.getDetailsDTO().getMessage())
                .authorName(dto.getDetailsDTO().getAuthorDetailsDTO().getName())
                .authorDate(dto.getDetailsDTO().getAuthorDetailsDTO().getDate())
                .repo(repo).build();
    }

    private String getFullNameFromHtmlUrl(String htmlUrl) {
        String[] parts = htmlUrl.split("/");
        return parts[3] + "/" + parts[4];
    }
}
