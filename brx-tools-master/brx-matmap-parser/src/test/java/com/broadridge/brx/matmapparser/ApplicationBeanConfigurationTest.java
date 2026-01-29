package com.broadridge.brx.matmapparser;

import com.broadridge.brx.matmapparser.finder.repositoryscan.fetch.FetchService;
import com.broadridge.brx.matmapparser.parsers.ExcelParser;
import com.broadridge.brx.matmapparser.parsers.loaders.GitlabRepositoryLogicalModelLoadingStrategy;
import com.broadridge.brx.matmapparser.parsers.loaders.LogicalModelLoadingStrategy;
import com.broadridge.brx.matmapparser.parsers.loaders.LogicalModelLoadingStrategyType;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gitlab4j.api.CommitsApi;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.RepositoryFileApi;
import org.gitlab4j.api.SearchApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.bind.Unmarshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationBeanConfigurationTest {

    private ApplicationBeanConfiguration applicationBeanConfiguration;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource resource;

    @Mock
    private FetchService fetchService;

    @BeforeEach
    void setUp() {
        this.applicationBeanConfiguration = new ApplicationBeanConfiguration();
    }

    @Test
    void gitLabApi() {
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxGitlabHost", "testgitlabhost");
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxGitlabAccessToken", "testgitlabaccesstoken");

        GitLabApi gitLabApi = applicationBeanConfiguration.gitLabApi();

        assertNotNull(gitLabApi);
    }

    @Test
    void searchApi() {
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxGitlabHost", "testgitlabhost");
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxGitlabAccessToken", "testgitlabaccesstoken");

        GitLabApi gitLabApi = applicationBeanConfiguration.gitLabApi();
        SearchApi searchApi = applicationBeanConfiguration.searchApi(gitLabApi);

        assertNotNull(searchApi);
    }

    @Test
    void repositoryFileApi() {
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxGitlabHost", "testgitlabhost");
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxGitlabAccessToken", "testgitlabaccesstoken");

        GitLabApi gitLabApi = applicationBeanConfiguration.gitLabApi();
        RepositoryFileApi repositoryFileApi = applicationBeanConfiguration.repositoryFileApi(gitLabApi);

        assertNotNull(repositoryFileApi);
    }

    @Test
    void commitsApi() {
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxGitlabHost", "testgitlabhost");
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxGitlabAccessToken", "testgitlabaccesstoken");

        GitLabApi gitLabApi = applicationBeanConfiguration.gitLabApi();
        CommitsApi commitsApi = applicationBeanConfiguration.commitsApi(gitLabApi);

        assertNotNull(commitsApi);
    }

    @Test
    void unmarshaller() throws Exception {
        Unmarshaller unmarshaller = applicationBeanConfiguration.unmarshaller();

        assertNotNull(unmarshaller);
    }

    @Test
    void localLogicalModelLoadingStrategy() {
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxLogicalModelLocalLocation", "testexcellocalfilelocation");
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxLogicalModelFilename", "testexcelfilefilename");

        LogicalModelLoadingStrategy logicalModelLoadingStrategy = applicationBeanConfiguration.localLogicalModelLoadingStrategy(resourceLoader);

        assertNotNull(logicalModelLoadingStrategy);
    }

    @Test
    void gitlabRepositoryLogicalModelLoadingStrategy() {
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxLogicalModelLocalLocation", "testexcelgitlabfilelocation");
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxLogicalModelFilename", "testexcelfilefilename");
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxLogicalModelExternalGitlabUrl", "http://testexcelgitlaburl.com");

        GitlabRepositoryLogicalModelLoadingStrategy gitlabRepositoryLogicalModelLoadingStrategy = applicationBeanConfiguration.gitlabRepositoryLogicalModelLoadingStrategy(fetchService);

        assertNotNull(gitlabRepositoryLogicalModelLoadingStrategy);
    }

    @Test
    void excelParser() throws IOException {
        when(resourceLoader.getResource(argThat(arg -> arg.contains("testexcellocalfilelocation")))).thenReturn(resource);

        XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(outputStream.toByteArray()));

        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxLogicalModelLocalLocation", "testexcellocalfilelocation");
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxLogicalModelExternalGitlabLocation", "testexcelgitlabfilelocation");
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxLogicalModelFilename", "testexcelfilefilename");
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxLogicalModelExternalGitlabUrl", "http://testexcelgitlaburl.com");
        ReflectionTestUtils.setField(applicationBeanConfiguration, "brxLogicalModelLoadingStrategy", "local");

        LogicalModelLoadingStrategy logicalModelLoadingStrategy = applicationBeanConfiguration.localLogicalModelLoadingStrategy(resourceLoader);
        GitlabRepositoryLogicalModelLoadingStrategy gitlabRepositoryLogicalModelLoadingStrategy = applicationBeanConfiguration.gitlabRepositoryLogicalModelLoadingStrategy(fetchService);

        Map<String, LogicalModelLoadingStrategy> logicalModelLoadingStrategies = new HashMap<>();
        logicalModelLoadingStrategies.put(LogicalModelLoadingStrategyType.LOCAL.getName(), logicalModelLoadingStrategy);
        logicalModelLoadingStrategies.put(LogicalModelLoadingStrategyType.GITLAB_REPOSITORY.getName(),  gitlabRepositoryLogicalModelLoadingStrategy);

        ExcelParser excelParser = applicationBeanConfiguration.excelParser(logicalModelLoadingStrategies);

        assertNotNull(excelParser);
    }

    @Test
    void openAPIV3Parser() {
        OpenAPIV3Parser openAPIV3Parser = applicationBeanConfiguration.openAPIV3Parser();

        assertNotNull(openAPIV3Parser);
    }
}