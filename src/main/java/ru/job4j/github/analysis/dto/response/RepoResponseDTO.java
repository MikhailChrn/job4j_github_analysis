package ru.job4j.github.analysis.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import ru.job4j.github.analysis.entity.RepoEntity;

import java.time.LocalDateTime;

/**
 * DTO for {@link RepoEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepoResponseDTO {

    @NotNull
    @URL
    @JsonProperty("html_url")
    String htmlUrl;

    @NotNull
    @JsonProperty("full_name")
    String fullName;

    @Past
    @JsonProperty("created_at")
    LocalDateTime createdAt;

    @JsonProperty("description")
    String description;

}