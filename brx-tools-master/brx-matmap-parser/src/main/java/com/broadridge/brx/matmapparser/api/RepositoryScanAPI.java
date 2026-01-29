package com.broadridge.brx.matmapparser.api;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.Repository;
import com.broadridge.brx.matmapparser.finder.repositoryscan.model.RepositoryUrls;
import com.broadridge.brx.matmapparser.processor.repository.RepositoryProcessable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RepositoryScanAPI {

    private final RepositoryProcessable repositoryProcessable;

    @PostMapping(value = "/repositories/matmap")
    public List<Repository> scanMatmaps(@Valid @RequestBody RepositoryUrls repositoryUrls) throws GitLabApiException {
        return repositoryProcessable.processMatmaps(repositoryUrls.urls);
    }

    @PostMapping(value = "/repositories/openapi-spec")
    public List<Repository> scanOpenAPISpecFiles(@Valid @RequestBody RepositoryUrls repositoryUrls) throws GitLabApiException {
        return repositoryProcessable.processOpenAPISpecFiles(repositoryUrls.urls);
    }
}
