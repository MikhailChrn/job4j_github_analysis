package ru.job4j.github.analysis.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitDetailsDTO {

    @JsonProperty("author")
    private CommitDetailsAuthorDTO authorDetailsDTO;

    @JsonProperty("message")
    private String message;

}
