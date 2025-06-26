package ru.job4j.github.analysis.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.URL;
import ru.job4j.github.analysis.entity.RepoEntity;

import java.time.LocalDateTime;

/**
 * DTO for {@link RepoEntity}
 */
@Value
@Builder
public class RepoRequestDTO {

    @URL
    String htmlUrl;

    @NotNull
    String fullName;

    @NotNull
    LocalDateTime createdAt;

    String description;

}