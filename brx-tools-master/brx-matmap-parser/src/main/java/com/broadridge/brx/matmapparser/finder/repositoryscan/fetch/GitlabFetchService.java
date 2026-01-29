package com.broadridge.brx.matmapparser.finder.repositoryscan.fetch;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.finder.repositoryscan.model.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.CommitsApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.RepositoryFileApi;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.RepositoryFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitlabFetchService implements FetchService {

    private final RepositoryFileApi repositoryFileApi;
    private final CommitsApi commitsApi;

    @Override
    public List<SpecFileInput> fetchFiles(final List<Repository> repositories) throws GitLabApiException {
        List<SpecFileInput> specFileInputs = new ArrayList<>();

        for (Repository repository : repositories) {
            for (String filePath : repository.getFilePaths()) {
                InputStreamResource fileContent = fetchFile(repository.getProjectPath(), filePath);
                Commit lastCommit = commitsApi.getCommits(repository.getProjectPath(), "HEAD", filePath).getFirst();
                SpecFileInput specFileInput = new SpecFileInput(filePath, lastCommit.getCommitterName(), lastCommit.getCommittedDate(), fileContent);
                specFileInputs.add(specFileInput);
            }
        }
        return specFileInputs;
    }

    @Override
    public InputStreamResource fetchLogicalModelFile(final String projectPath, final String filePath) throws GitLabApiException {
        return fetchFile(projectPath, filePath);
    }

    private InputStreamResource fetchFile(final String projectPath, final String filePath) throws GitLabApiException {
        RepositoryFile repositoryFile = repositoryFileApi.getFile(projectPath, filePath, "HEAD");
        log.info("Fetching file: {} from project: {}", filePath, projectPath);
        return new InputStreamResource(new ByteArrayInputStream(repositoryFile.getDecodedContentAsBytes()));
    }
}
