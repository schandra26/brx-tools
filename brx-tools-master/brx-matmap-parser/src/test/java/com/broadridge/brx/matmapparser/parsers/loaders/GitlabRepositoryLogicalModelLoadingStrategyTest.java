package com.broadridge.brx.matmapparser.parsers.loaders;

import com.broadridge.brx.matmapparser.finder.repositoryscan.fetch.FetchService;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.RepositoryFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitlabRepositoryLogicalModelLoadingStrategyTest {

    @Mock
    FetchService fetchServiceMock;

    String gitlabRepositoryURL = "https://testurl.com/test/project";
    String excelFileLocation = "test/location/logical-model.xlsx";

    @Test
    void loadLogicalModel() throws GitLabApiException, URISyntaxException, IOException {
        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(getRepositoryFileMock().getDecodedContentAsBytes()));
        when(fetchServiceMock.fetchLogicalModelFile(anyString(), anyString())).thenReturn(inputStreamResource);

        URL gitlabRepositoryURLObj = new URI(gitlabRepositoryURL).toURL();

        GitlabRepositoryLogicalModelLoadingStrategy gitlabRepositoryLogicalModelLoadingStrategy = new GitlabRepositoryLogicalModelLoadingStrategy(gitlabRepositoryURLObj, excelFileLocation, fetchServiceMock);

        InputStream inputStream = gitlabRepositoryLogicalModelLoadingStrategy.loadLogicalModel();

        assertNotNull(inputStream);
    }

    private RepositoryFile getRepositoryFileMock() {
        RepositoryFile repositoryFile = new RepositoryFile();
        repositoryFile.encodeAndSetContent("test");

        return repositoryFile;
    }
}
