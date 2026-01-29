package com.broadridge.brx.matmapparser.finder.repositoryscan.search;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.Repository;
import org.gitlab4j.api.SearchApi;
import org.gitlab4j.api.models.SearchBlob;
import org.gitlab4j.models.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitlabSearchServiceTest {

    @Mock
    private SearchApi searchApiMock;

    @InjectMocks
    private GitlabSearchService gitlabSearchService;

    @Test
    void searchFiles() throws Exception {
        List<String> projectPaths = List.of("repository/project1");
        SearchBlob searchBlobOne = new SearchBlob();
        searchBlobOne.setFilename("1.matmap");
        SearchBlob searchBlobTwo = new SearchBlob();
        searchBlobTwo.setFilename("2.matmap");
        List<SearchBlob> searchBlobs = List.of(searchBlobOne, searchBlobTwo);

        when(searchApiMock.projectSearchStream(
                anyString(),
                any(Constants.ProjectSearchScope.class),
                anyString())
        ).thenReturn(searchBlobs.stream());

        List<Repository> repositories = gitlabSearchService.searchFiles(projectPaths, List.of(GitlabSearchType.MATMAP));

        assertEquals(1, repositories.size());
        assertEquals("repository/project1", repositories.getFirst().getProjectPath());
        assertEquals("1.matmap", repositories.getFirst().getFilePaths().getFirst());
        assertEquals("2.matmap", repositories.getFirst().getFilePaths().get(1));
    }
}