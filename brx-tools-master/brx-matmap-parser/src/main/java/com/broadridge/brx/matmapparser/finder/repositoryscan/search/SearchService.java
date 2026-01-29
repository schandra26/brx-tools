package com.broadridge.brx.matmapparser.finder.repositoryscan.search;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.Repository;
import org.gitlab4j.api.GitLabApiException;

import java.util.List;

public interface SearchService {

    List<Repository> searchFiles(final List<String> projectPaths, final List<GitlabSearchType> gitlabSearchTypes) throws GitLabApiException;

}