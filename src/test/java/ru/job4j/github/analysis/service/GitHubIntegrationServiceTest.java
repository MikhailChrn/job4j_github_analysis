package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.job4j.github.analysis.entity.CommitEntity;
import ru.job4j.github.analysis.entity.RepoEntity;
import ru.job4j.github.analysis.repository.CommitRepository;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * MockRestServiceServer:
 * - Используется для имитации удалённых HTTP-сервисов,
 *      с которыми ты взаимодействуешь, например, через RestTemplate.
 * - Помогает тестировать компоненты, которые отправляют HTTP-запросы наружу,
 *      и получать предсказуемые ответы.
 * - Не используется для тестирования контроллеров!
 *      Это тестирование клиента, а не сервера.
 * - Используется с @RestClientTest или вручную с RestTemplate.
 *
 * ДЛЯ ТЕСТИРОВАНИЯ ИСПОЛЬЗУЮТСЯ ШАБЛОНЫ
 * 'src/test/resources/jsonsample/reposample.json'
 * 'src/test/resources/jsonsample/commitssample.json'
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GitHubIntegrationServiceTest {

    private MockRestServiceServer mockServer;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private FileService fileService;

    @Autowired
    private RepoRepository repoRepository;

    @Autowired
    private RepoService repoService;

    @Autowired
    private CommitRepository commitRepository;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void whenFetchCommitsFromMockThenGetListOfCommitsEntity() throws IOException {
        String urlRepoRequest = "https://api.github.com/repos/MikhailChrn/job4j_social_media_api";
        String urlCommitsRequest = "https://api.github.com/repos/MikhailChrn/job4j_social_media_api/commits";
        String fullRepoName = "MikhailChrn/job4j_social_media_api";

        String jsonBodyResponseForRepo = fileService.readFileContent("jsonsample/reposample.json");
        String jsonBodyResponseForCommits = fileService.readFileContent("jsonsample/commitssample.json");

        mockServer.expect(requestTo(urlRepoRequest))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonBodyResponseForRepo, MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(urlCommitsRequest))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonBodyResponseForCommits, MediaType.APPLICATION_JSON));

        Optional<RepoEntity> optionalRepoEntity = gitHubService.fetchRepo(fullRepoName);
        assertTrue(optionalRepoEntity.isPresent());
        repoRepository.save(optionalRepoEntity.get());

        List<CommitEntity> commitEntityList = gitHubService.fetchAllCommits(fullRepoName);
        assertEquals(13, commitEntityList.size());

        commitEntityList.forEach(commitRepository::save);

        repoService.findLastCommitByRepoFullName(fullRepoName);

        System.out.println("Крайний коммит : " + repoRepository.findAllByFullName(fullRepoName)
                .stream().findFirst().get().getLastCommitUrl());
    }
}