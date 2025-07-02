package ru.job4j.github.analysis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.job4j.github.analysis.dto.response.CommitResponseDTO;
import ru.job4j.github.analysis.dto.response.RepoResponseDTO;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;
import ru.job4j.github.analysis.mapper.CommitMapper;
import ru.job4j.github.analysis.mapper.RepoMapper;
import ru.job4j.github.analysis.repository.CommitRepository;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Аннотация @ExtendWith(MockitoExtension.class) используется в тестах на JUnit 5
 * (JUnit Jupiter) и подключает расширение Mockito к тестовому классу.
 * Без этой аннотации Mockito не сможет автоматически обрабатывать @Mock и @InjectMocks,
 * и вы получите ошибку (например, NullPointerException), потому что объекты не будут инициализированы.
 * Если используется JUnit 4, аналогичной аннотацией будет @RunWith(MockitoJUnitRunner.class).
 *
 * Аннотация @TestInstance(TestInstance.Lifecycle.PER_CLASS) используется в JUnit 5 (Jupiter)
 *      и определяет жизненный цикл экземпляров класса с тестами.
 * - По умолчанию JUnit 5 создаёт новый экземпляр тестового класса для каждого тестового метода
 *      (поведение по умолчанию: PER_METHOD). Это значит, что каждый тестовый метод изолирован
 *      и выполняется в своём собственном экземпляре класса.
 * - Если вы укажете @TestInstance(TestInstance.Lifecycle.PER_CLASS),
 *      JUnit создаёт только один экземпляр тестового класса на весь класс. Это позволяет:
 *          Использовать нестатические методы с аннотацией @BeforeAll и @AfterAll (по умолчанию они должны быть static).
 *          Хранить общее состояние между тестами (аккуратно с этим! Может нарушить изоляцию тестов).
 *          Ускорить выполнение, когда создание экземпляра класса дорогая операция.
 */

@SpringBootTest(properties = "spring.liquibase.enabled=false")
@ExtendWith(MockitoExtension.class)
class CommitServiceTest {

    @Autowired
    private FileService fileService;

    @Autowired
    private RepoMapper repoMapper;

    @Mock
    private CommitRepository commitRepository;

    @Mock
    private RepoRepository repoRepository;

    @Mock
    private GitHubService gitHubService;

    @InjectMocks
    private CommitMapper commitMapper;

    @InjectMocks
    private CommitService commitService;

    static ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    public void whenRequestRecentCommitsFromMockThenGetListOfCommitsEntity() throws IOException {

        String jsonForRepo = fileService.readFileContent("jsonsample/reposample.json");
        String jsonForExternalCommits = fileService.readFileContent("jsonsample/commitsfullsample.json");
        String jsonForInternalCommits = fileService.readFileContent("jsonsample/commitsnotfullsample.json");

        RepoEntity repoTestEntity = repoMapper.getEntityFromResponseDto(
                objectMapper.readValue(jsonForRepo,
                        new TypeReference<RepoResponseDTO>() {
                        }));

        when(repoRepository.findAllByFullName(repoTestEntity.getFullName())).thenReturn(List.of(repoTestEntity));

        List<CommitEntity> commitExternalTestEntities = objectMapper.readValue(jsonForExternalCommits,
                        new TypeReference<List<CommitResponseDTO>>() {
                        }).stream().map(commitMapper::getEntityFromResponseDto).toList();

        List<CommitEntity> commitInternalTestEntities = objectMapper.readValue(jsonForInternalCommits,
                        new TypeReference<List<CommitResponseDTO>>() {
                        }).stream().map(commitMapper::getEntityFromResponseDto).toList();

        when(gitHubService.fetchAllCommits(repoTestEntity.getFullName()))
                .thenReturn(commitExternalTestEntities);
        when(commitRepository.findAllByRepoFullName(repoTestEntity.getFullName()))
                .thenReturn(commitInternalTestEntities);

        List<CommitEntity> recentCommits = commitService.getRecentCommits(repoTestEntity.getFullName());
        assertEquals(6, recentCommits.size());
    }
}