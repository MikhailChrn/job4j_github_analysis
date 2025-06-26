package ru.job4j.github.analysis.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import ru.job4j.github.analysis.entity.CommitEntity;

/**
 * DTO for {@link CommitEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitResponseDTO {

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("commit")
    private CommitDetailsDTO detailsDTO;




}
