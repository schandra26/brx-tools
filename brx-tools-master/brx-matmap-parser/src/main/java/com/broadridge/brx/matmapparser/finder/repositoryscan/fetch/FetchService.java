package com.broadridge.brx.matmapparser.finder.repositoryscan.fetch;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.finder.repositoryscan.model.Repository;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.core.io.InputStreamResource;

import java.util.List;

public interface FetchService {

    /**
     * Fetches the matmap files from a list of Gitlab project repository
     *
     * @param repository list of Repository objects containing the gitlab project path associated to a list of paths to
     *                   the matmap files that are being fetched
     * @return a list of SpecFileInput objects containing the the name of the files that were fetched together with the
     *                   actual content of the files
     * @throws GitLabApiException exception thrown by the Gitlab API used to fetch the matmap files
     */
    List<SpecFileInput> fetchFiles(final List<Repository> repository) throws GitLabApiException;

    /**
     * Fetches the logical model file from a Gitlab project repository
     *
     * @param projectPath the path to the project repository to fetch the logical model from
     * @param filePath the path to the logical model excel file
     * @return the content of the logical model excel file as an InputStreamResource object
     * @throws GitLabApiException exception thrown by the Gitlab API used to fetch the logical model file
     */
    InputStreamResource fetchLogicalModelFile(final String projectPath, final String filePath) throws GitLabApiException;

}
