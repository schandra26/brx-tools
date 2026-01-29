package com.broadridge.brx.matmapparser.finder.repositoryscan.search;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.SearchApi;
import org.gitlab4j.api.models.SearchBlob;
import org.gitlab4j.models.Constants;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitlabSearchService implements SearchService {

    private final SearchApi searchApi;

    @Override
    public List<Repository> searchFiles(final List<String> projectPaths, final List<GitlabSearchType> gitlabSearchTypes) throws GitLabApiException {
        final List<Repository> repositories = new ArrayList<>();

        for (final String projectPath : projectPaths) {
            final List<String> filePaths = new ArrayList<>();
            for (final GitlabSearchType gitlabSearchType : gitlabSearchTypes) {
                log.info("Searching for {} files in project: {}", gitlabSearchType.getExtension(), projectPath);
                final List<String> extensionFilePaths = searchApi.projectSearchStream(projectPath, Constants.ProjectSearchScope.BLOBS, "extension:" + gitlabSearchType.getExtension())
                        .map(SearchBlob::getFilename)
                        .toList();
                filePaths.addAll(extensionFilePaths);
            }
            Repository repository = new Repository(projectPath, filePaths);
            log.info("Found {}", repository.getFilePaths());
            repositories.add(repository);
        }
        return repositories;
    }
}
