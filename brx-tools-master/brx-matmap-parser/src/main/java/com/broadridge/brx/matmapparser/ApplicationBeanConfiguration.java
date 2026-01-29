package com.broadridge.brx.matmapparser;

import com.broadridge.brx.matmapparser.finder.repositoryscan.fetch.FetchService;
import com.broadridge.brx.matmapparser.model.matmap.Translator;
import com.broadridge.brx.matmapparser.parsers.BRxLogicalModelExcelParser;
import com.broadridge.brx.matmapparser.parsers.ExcelParser;
import com.broadridge.brx.matmapparser.parsers.loaders.GitlabRepositoryLogicalModelLoadingStrategy;
import com.broadridge.brx.matmapparser.parsers.loaders.LocalLogicalModelLoadingStrategy;
import com.broadridge.brx.matmapparser.parsers.loaders.LogicalModelLoadingStrategy;
import com.broadridge.brx.matmapparser.parsers.loaders.LogicalModelLoadingStrategyType;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.SneakyThrows;
import org.gitlab4j.api.CommitsApi;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.RepositoryFileApi;
import org.gitlab4j.api.SearchApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.URI;
import java.net.URL;
import java.util.Map;

@Configuration
public class ApplicationBeanConfiguration {

    @Value("${brx.logical.model.filename}")
    private String brxLogicalModelFilename;

    @Value("${brx.logical.model.local.location}")
    private String brxLogicalModelLocalLocation;

    @Value("${brx.logical.model.external.gitlab.url}")
    private String brxLogicalModelExternalGitlabUrl;

    @Value("${brx.logical.model.external.gitlab.location}")
    private String brxLogicalModelExternalGitlabLocation;

    @Value("${brx.logical.model.ignored.sheets}")
    private String[] brxLogicalModelIgnoredSheets;

    @Value("${brx.logical.model.loading.strategy}")
    private String brxLogicalModelLoadingStrategy;

    @Value("${brx.gitlab.access.token}")
    private String brxGitlabAccessToken;

    @Value("${brx.gitlab.host}")
    private String brxGitlabHost;

    @Bean
    public GitLabApi gitLabApi() {
        return new GitLabApi(brxGitlabHost, brxGitlabAccessToken);
    }

    @Bean
    public SearchApi searchApi(GitLabApi gitLabApi) {
        return gitLabApi.getSearchApi();
    }

    @Bean
    public RepositoryFileApi repositoryFileApi(GitLabApi gitLabApi) {
        return gitLabApi.getRepositoryFileApi();
    }

    @Bean
    public CommitsApi commitsApi(GitLabApi gitLabApi) {
        return gitLabApi.getCommitsApi();
    }

    @Bean
    public Unmarshaller unmarshaller() throws JAXBException {
        return JAXBContext.newInstance(Translator.class).createUnmarshaller();
    }

    @Bean("local")
    public LogicalModelLoadingStrategy localLogicalModelLoadingStrategy(ResourceLoader resourceLoader) {
        String filePath = brxLogicalModelLocalLocation + brxLogicalModelFilename;
        return new LocalLogicalModelLoadingStrategy(resourceLoader, filePath);
    }

    @Bean("gitlabRepository")
    @SneakyThrows
    public GitlabRepositoryLogicalModelLoadingStrategy gitlabRepositoryLogicalModelLoadingStrategy(final FetchService fetchService) {
        final URL logicalModelExternalGitlabURLObj = new URI(brxLogicalModelExternalGitlabUrl).toURL();
        String filePath = brxLogicalModelExternalGitlabLocation + brxLogicalModelFilename;
        return new GitlabRepositoryLogicalModelLoadingStrategy(logicalModelExternalGitlabURLObj, filePath, fetchService);
    }

    @Bean
    public ExcelParser excelParser(Map<String, LogicalModelLoadingStrategy> logicalModelLoadingStrategies) {
        return new BRxLogicalModelExcelParser(brxLogicalModelIgnoredSheets, logicalModelLoadingStrategies, LogicalModelLoadingStrategyType.getValueByName(brxLogicalModelLoadingStrategy));
    }

    @Bean
    public OpenAPIV3Parser openAPIV3Parser() {
        return new OpenAPIV3Parser();
    }

}
