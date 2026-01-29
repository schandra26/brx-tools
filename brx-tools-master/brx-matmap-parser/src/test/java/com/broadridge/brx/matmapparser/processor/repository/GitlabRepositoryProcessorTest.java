package com.broadridge.brx.matmapparser.processor.repository;

import com.broadridge.brx.matmapparser.finder.repositoryscan.fetch.FetchService;
import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.finder.repositoryscan.model.Repository;
import com.broadridge.brx.matmapparser.finder.repositoryscan.search.SearchService;
import com.broadridge.brx.matmapparser.model.StrategyContext;
import com.broadridge.brx.matmapparser.processor.file.SpecFileProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitlabRepositoryProcessorTest {

    @Mock
    private SearchService searchServiceMock;
    @Mock
    private FetchService fetchServiceMock;
    @Mock
    private SpecFileProcessor specFileProcessor;

    @Captor
    ArgumentCaptor<List<String>> projectPathsCaptor;

    @InjectMocks
    private GitlabRepositoryProcessor gitlabRepositoryProcessor;

    @Test
    void processMatmaps() throws Exception {
        List<URL> repositoryUrls = List.of(
                URI.create("https://test.com/repository/url").toURL(),
                URI.create("https://test.com/repository/url-two").toURL()
        );
        List<Repository> expectedRepositories = List.of(
                new Repository("/repository/url", Stream.of("mappings/base/1.matmap", "matmaps/2.matmap").collect(Collectors.toCollection(ArrayList::new))),
                new Repository("/repository/url-two", Stream.of("mappings/3.matmap", "matmaps/4.matmap").collect(Collectors.toCollection(ArrayList::new)))
        );

        when(searchServiceMock.searchFiles(anyList(), anyList())).thenReturn(expectedRepositories);

        final List<SpecFileInput> specFileInputs = List.of(
                new SpecFileInput("mappings/base/1.matmap", "John Doe", new Date(), new InputStreamResource(new ByteArrayResource("test".getBytes()))),
                new SpecFileInput("matmaps/2.matmap", "John Doe", new Date(), new InputStreamResource(new ByteArrayResource("test".getBytes()))),
                new SpecFileInput("mappings/3.matmap", "John Doe", new Date(), new InputStreamResource(new ByteArrayResource("test".getBytes()))),
                new SpecFileInput("matmaps/4.matmap", "John Doe", new Date(), new InputStreamResource(new ByteArrayResource("test".getBytes())))
        );

        when(fetchServiceMock.fetchFiles(anyList())).thenReturn(specFileInputs);
        doNothing().when(specFileProcessor).processSpecFileInputs(anyList(), Mockito.any(StrategyContext.class));

        List<Repository> repositories = gitlabRepositoryProcessor.processMatmaps(repositoryUrls);

        verify(searchServiceMock).searchFiles(projectPathsCaptor.capture(), anyList());
        List<String> projectPaths = projectPathsCaptor.getValue();
        assertEquals(projectPaths.size(), repositoryUrls.size());
        assertEquals("repository/url", projectPaths.getFirst());
        assertEquals("repository/url-two", projectPaths.get(1));

        assertIterableEquals(expectedRepositories, repositories);
        assertEquals(expectedRepositories.getFirst().getProjectPath(), repositories.getFirst().getProjectPath());
        assertIterableEquals(expectedRepositories.getFirst().getFilePaths(), repositories.getFirst().getFilePaths());
    }

    @Test
    void processOpenAPISpecFiles() throws Exception {
        List<URL> repositoryUrls = List.of(
                URI.create("https://test.com/repository/url").toURL(),
                URI.create("https://test.com/repository/url-two").toURL()
        );
        List<Repository> expectedRepositories = List.of(
                new Repository("/repository/url", Stream.of("openapi/base/1.yaml", "openapi/2.yml").collect(Collectors.toCollection(ArrayList::new))),
                new Repository("/repository/url-two", Stream.of("openapi/3.yaml", "openapi/4.yaml").collect(Collectors.toCollection(ArrayList::new)))
        );

        when(searchServiceMock.searchFiles(anyList(), anyList())).thenReturn(expectedRepositories);

        final List<SpecFileInput> specFileInputs = List.of(
                new SpecFileInput("openapi/base/1.yaml", "John Doe", new Date(), new InputStreamResource(new ByteArrayResource("test".getBytes()))),
                new SpecFileInput("openapi/2.yml", "John Doe", new Date(), new InputStreamResource(new ByteArrayResource("test".getBytes()))),
                new SpecFileInput("openapi/3.yaml", "John Doe", new Date(), new InputStreamResource(new ByteArrayResource("test".getBytes()))),
                new SpecFileInput("openapi/4.yaml", "John Doe", new Date(), new InputStreamResource(new ByteArrayResource("test".getBytes())))
        );

        when(fetchServiceMock.fetchFiles(anyList())).thenReturn(specFileInputs);
        doNothing().when(specFileProcessor).processSpecFileInputs(anyList(), Mockito.any(StrategyContext.class));

        List<Repository> repositories = gitlabRepositoryProcessor.processOpenAPISpecFiles(repositoryUrls);

        verify(searchServiceMock, atLeastOnce()).searchFiles(projectPathsCaptor.capture(), anyList());
        List<String> projectPaths = projectPathsCaptor.getValue();
        assertEquals(projectPaths.size(), repositoryUrls.size());
        assertEquals("repository/url", projectPaths.getFirst());
        assertEquals("repository/url-two", projectPaths.get(1));

        assertIterableEquals(expectedRepositories, repositories);
        assertEquals(expectedRepositories.getFirst().getProjectPath(), repositories.getFirst().getProjectPath());
        assertIterableEquals(expectedRepositories.getFirst().getFilePaths(), repositories.getFirst().getFilePaths());
    }
}