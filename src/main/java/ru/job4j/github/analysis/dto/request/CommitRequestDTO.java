package ru.job4j.github.analysis.dto.request;

import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.URL;
import ru.job4j.github.analysis.entity.CommitEntity;

import java.time.LocalDateTime;

/**
 * DTO for {@link CommitEntity}
 */
@Value
@Builder
public class CommitRequestDTO {

    String fullNameRepo;

    @URL
    String htmlUrl;

    String message;

    String authorName;

    LocalDateTime authorDate;

}