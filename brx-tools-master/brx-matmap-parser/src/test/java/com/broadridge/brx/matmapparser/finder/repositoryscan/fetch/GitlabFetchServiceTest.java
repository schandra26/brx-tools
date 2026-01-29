package com.broadridge.brx.matmapparser.finder.repositoryscan.fetch;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.finder.repositoryscan.model.Repository;
import org.gitlab4j.api.CommitsApi;
import org.gitlab4j.api.RepositoryFileApi;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.RepositoryFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitlabFetchServiceTest {

    @Mock
    private RepositoryFileApi repositoryFileApiMock;
    @Mock
    private CommitsApi commitsApiMock;

    @InjectMocks
    private GitlabFetchService gitlabFetchService;

    @Test
    void fetchFiles() throws Exception {
        List<Repository> repositories = List.of(
                new Repository("/test/project", List.of("/mapping/test.matmap", "/mapping/test2.matmap")),
                new Repository("/test2/project", List.of("/mapping/test3.matmap", "/mapping/test4.matmap"))
        );

        List<Commit> commits = getCommits();

        when(repositoryFileApiMock.getFile(anyString(), anyString(), anyString())).thenReturn(getRepositoryFileMock());
        when(commitsApiMock.getCommits(anyString(), anyString(), anyString())).thenReturn(commits);

        List<SpecFileInput> specFileInputs = gitlabFetchService.fetchFiles(repositories);

        for (SpecFileInput specFileInput : specFileInputs) {
            assertEquals("test", specFileInput.getContents().getContentAsString(StandardCharsets.UTF_8));
            assertEquals("John Doe", specFileInput.getLastCommitter());
        }
    }

    @Test
    void fetchLogicalModelFile() throws Exception {
        final String projectPath = "/test/project";
        final String filePath = "/test/logical-model.xlsx";

        when(repositoryFileApiMock.getFile(anyString(), anyString(), anyString())).thenReturn(getRepositoryFileMock());

        InputStreamResource inputStreamResource = gitlabFetchService.fetchLogicalModelFile(projectPath, filePath);

        assertEquals("test", inputStreamResource.getContentAsString(StandardCharsets.UTF_8));
    }

    private RepositoryFile getRepositoryFileMock() {
        RepositoryFile repositoryFile = new RepositoryFile();
        repositoryFile.encodeAndSetContent("test");
        repositoryFile.setLastCommitId(UUID.randomUUID().toString());

        return repositoryFile;
    }

    private List<Commit> getCommits() {
        List<Commit> commits = new ArrayList<>();
        Commit commit = new Commit();
        commit.setCommitterName("John Doe");
        commits.add(commit);
        return commits;
    }
}