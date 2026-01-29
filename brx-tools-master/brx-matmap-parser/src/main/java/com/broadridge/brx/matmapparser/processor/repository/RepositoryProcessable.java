package com.broadridge.brx.matmapparser.processor.repository;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.Repository;
import org.gitlab4j.api.GitLabApiException;

import java.net.URL;
import java.util.List;

public interface RepositoryProcessable {

    List<Repository> processMatmaps(final List<URL> repositoryUrls) throws GitLabApiException;

    List<Repository> processOpenAPISpecFiles(final List<URL> repositoryUrls) throws GitLabApiException;

}
