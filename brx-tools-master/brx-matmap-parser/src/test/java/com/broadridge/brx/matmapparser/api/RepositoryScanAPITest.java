package com.broadridge.brx.matmapparser.api;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.Repository;
import com.broadridge.brx.matmapparser.finder.repositoryscan.model.RepositoryUrls;
import com.broadridge.brx.matmapparser.processor.repository.RepositoryProcessable;
import org.gitlab4j.api.GitLabApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryScanAPITest {

    @Mock
    private RepositoryProcessable repositoryProcessableMock;

    @InjectMocks
    private RepositoryScanAPI repositoryScanAPI;

    @Test
    void scanMatmaps() throws Exception {
        RepositoryUrls repositoryUrls = new RepositoryUrls(
                List.of(URI.create("https://test.com/repostiory/url").toURL(),
                        URI.create("https://test.com/repositories/url-two").toURL()
        ));
        List<Repository> expectedRepositories = List.of(
                new Repository("/repository/url", List.of("mappings/base/1.matmap", "matmaps/2.matmap")),
                new Repository("/repository/url-two", List.of("mappings/3.matmap", "matmaps/4.matmap"))
        );

        when(repositoryProcessableMock.processMatmaps(repositoryUrls.urls)).thenReturn(expectedRepositories);

        List<Repository> repositories = repositoryScanAPI.scanMatmaps(repositoryUrls);

        assertIterableEquals(expectedRepositories, repositories);
        assertEquals(expectedRepositories.getFirst().getProjectPath(), repositories.getFirst().getProjectPath());
        assertIterableEquals(expectedRepositories.getFirst().getFilePaths(), repositories.getFirst().getFilePaths());
    }

    @Test
    void scanMatmaps_throwsGitlabApiException() throws Exception {
        RepositoryUrls repositoryUrls = new RepositoryUrls(
                List.of(URI.create("https://test.com/repostiory/url").toURL(),
                        URI.create("https://test.com/repositories/url-two").toURL()
                )
        );

        when(repositoryProcessableMock.processMatmaps(anyList())).thenThrow(new GitLabApiException("404 Not Found"));

        Exception exception = assertThrows(GitLabApiException.class, () -> repositoryScanAPI.scanMatmaps(repositoryUrls));

        assertEquals("404 Not Found", exception.getMessage());
    }
}
