package ru.job4j.github.analysis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

import ru.job4j.github.analysis.dto.FullRepoNameDTO;
import ru.job4j.github.analysis.dto.request.CommitRequestDTO;
import ru.job4j.github.analysis.dto.request.RepoRequestDTO;
import ru.job4j.github.analysis.service.RepoService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 *  MockMvc: *
 *  -Используется для тестирования веб-контроллеров внутри Spring MVC.
 *  -Позволяет запускать HTTP-запросы к контроллерам внутри контекста Spring
 *          без поднятия настоящего веб-сервера (без Tomcat, Undertow и др.).
 *  -Полезен, когда тебе нужно протестировать поведение контроллеров: маршруты,
 *          заголовки, параметры, ответы, коды статусов и т. п.
 *  -Используется вместе с аннотациями вроде @WebMvcTest.
 */

@WebMvcTest
@AutoConfigureMockMvc
class GitHubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepoService repoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenGetAllRepositoriesThenReturnsListOfRepos() throws Exception {
        List<RepoRequestDTO> response = List.of(
                RepoRequestDTO.builder().fullName("repo0").build(),
                RepoRequestDTO.builder().fullName("repo1").build());

        Mockito.when(repoService.findAllrepositories()).thenReturn(response);

        mockMvc.perform(get("/api/repos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("repo0"))
                .andExpect(jsonPath("$[1].fullName").value("repo1"));
    }

    @Test
    void whenGetAllCommitsByRepoFullNameThenReturnsListOfCommits() throws Exception {
        List<CommitRequestDTO> commits = List.of(
                CommitRequestDTO.builder().htmlUrl("test_url_0").build(),
                CommitRequestDTO.builder().htmlUrl("test_url_1").build()
        );

        FullRepoNameDTO dto = new FullRepoNameDTO("owner/repo");

        Mockito.when(repoService.findAllCommitsByRepoFullName(dto.getFullName()))
                .thenReturn(commits);

        mockMvc.perform(get("/api/commits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].htmlUrl").value("test_url_0"))
                .andExpect(jsonPath("$[1].htmlUrl").value("test_url_1"));
    }

    @Test
    void whenCreateMethodCallThanReturnsCreatedMessage() throws Exception {
        FullRepoNameDTO dto = new FullRepoNameDTO("owner/repo");

        Mockito.when(repoService.create(dto.getFullName())).thenReturn(1);

        mockMvc.perform(post("/api/repos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Репозиторий owner/repo успешно добавлен"));
    }
}