package com.broadridge.brx.matmapparser.parsers.loaders;


import com.broadridge.brx.matmapparser.finder.repositoryscan.fetch.FetchService;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URL;

import static com.broadridge.brx.matmapparser.util.PathUtil.getProjectPathFromRepositoryURL;

public class GitlabRepositoryLogicalModelLoadingStrategy implements LogicalModelLoadingStrategy {

    private final URL gitlabRepositoryURL;
    private final String excelFileLocation;
    private final FetchService fetchService;

    public GitlabRepositoryLogicalModelLoadingStrategy(final URL gitlabRepositoryURL,
                                                       final String excelFileLocation,
                                                       final FetchService fetchService) {
        this.gitlabRepositoryURL = gitlabRepositoryURL;
        this.excelFileLocation = excelFileLocation;
        this.fetchService = fetchService;
    }

    @Override
    @SneakyThrows
    public InputStream loadLogicalModel() {
        return fetchService.fetchLogicalModelFile(getProjectPathFromRepositoryURL(gitlabRepositoryURL), excelFileLocation).getInputStream();
    }
}
