package com.broadridge.brx.matmapparser.processor.repository;

import com.broadridge.brx.matmapparser.finder.repositoryscan.fetch.FetchService;
import com.broadridge.brx.matmapparser.finder.repositoryscan.model.Repository;
import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.finder.repositoryscan.search.GitlabSearchType;
import com.broadridge.brx.matmapparser.finder.repositoryscan.search.SearchService;
import com.broadridge.brx.matmapparser.util.PathUtil;
import com.broadridge.brx.matmapparser.model.StrategyContext;
import com.broadridge.brx.matmapparser.processor.file.FileProcessor;
import lombok.RequiredArgsConstructor;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;

import static com.broadridge.brx.matmapparser.extractors.MappingExtractorStrategyType.DIRECT_BRX;
import static com.broadridge.brx.matmapparser.extractors.MappingExtractorStrategyType.DIRECT_OPEN_API;
import static com.broadridge.brx.matmapparser.parsers.spec.SpecFileParsingStrategyType.MATMAP;
import static com.broadridge.brx.matmapparser.parsers.spec.SpecFileParsingStrategyType.OPEN_API;
import static com.broadridge.brx.matmapparser.persistence.PersistenceStrategyType.DATABASE_MATMAP;
import static com.broadridge.brx.matmapparser.persistence.PersistenceStrategyType.DATABASE_OPENAPI;
@Service
@RequiredArgsConstructor
public class GitlabRepositoryProcessor implements RepositoryProcessable {

    private final SearchService searchService;
    private final FetchService fetchService;
    private final FileProcessor fileProcessor;

    @Override
    public List<Repository> processMatmaps(final List<URL> repositoryUrls) throws GitLabApiException {
        return processFilesFromRepository(repositoryUrls, new StrategyContext(MATMAP, DIRECT_BRX, DATABASE_MATMAP), List.of(GitlabSearchType.MATMAP));
    }

    @Override
    public List<Repository> processOpenAPISpecFiles(final List<URL> repositoryUrls) throws GitLabApiException {
        return processFilesFromRepository(repositoryUrls, new StrategyContext(OPEN_API, DIRECT_OPEN_API, DATABASE_OPENAPI), List.of(GitlabSearchType.YAML, GitlabSearchType.YML));
    }

    private List<Repository> processFilesFromRepository(final List<URL> repositoryUrls, final StrategyContext strategyContext, final List<GitlabSearchType> gitlabSearchTypes) throws GitLabApiException {
        final List<String> projectPaths = getProjectPaths(repositoryUrls);
        final List<Repository> repositories = searchService.searchFiles(projectPaths, gitlabSearchTypes);
        final List<SpecFileInput> specFileInputs = fetchService.fetchFiles(repositories);
        fileProcessor.processSpecFileInputs(specFileInputs, strategyContext);
        /*dev-note : there will be yaml files that are being fetched, but not processed as they are not openapi specs, and we need
         * to take them out of the repositories list that is being returned*/
        cleanUpUnprocessedRepositories(repositories, specFileInputs);
        return repositories;
    }

    private List<String> getProjectPaths(List<URL> repositoryUrls) {
        return repositoryUrls
                .stream()
                .map(PathUtil::getProjectPathFromRepositoryURL)
                .toList();
    }

    private void cleanUpUnprocessedRepositories(final List<Repository> repositories, final List<SpecFileInput> specFileInputs) {
        for (final Repository repository : repositories) {
            final List<String> unprocessedFilePaths = new ArrayList<>();
            for (final String filePath : repository.getFilePaths()) {
                boolean isFilePathProcessed = false;
                for (final SpecFileInput specFileInput : specFileInputs) {
                    if (specFileInput.getFilePath().equals(filePath)) {
                        isFilePathProcessed = true;
                        break;
                    }
                }
                if (!isFilePathProcessed) {
                    unprocessedFilePaths.add(filePath);
                }
            }
            repository.getFilePaths().removeAll(unprocessedFilePaths);
        }
    }
}
