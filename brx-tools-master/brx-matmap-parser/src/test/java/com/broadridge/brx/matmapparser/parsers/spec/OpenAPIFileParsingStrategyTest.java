package com.broadridge.brx.matmapparser.parsers.spec;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.model.SpecFile;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenAPIFileParsingStrategyTest {

    @Mock
    private Resource resourceMock;
    @Mock
    private InputStreamResource inputStreamResourceMock;
    @Mock
    private SpecFileInput specFileInputMock;
    @Mock
    private OpenAPIV3Parser openAPIV3ParserMock;

    @InjectMocks
    private OpenAPIFileParsingStrategy openAPIFileParsingStrategy;

    @Test
    void parseResource() {
        List<Resource> resources = new ArrayList<>(Collections.singletonList(resourceMock));

        when(openAPIV3ParserMock.readContents(any(), any(), any(ParseOptions.class))).thenReturn(getParseResultYamlContent(getOpenAPIYamlContent()));
        when(resourceMock.getFilename()).thenReturn("test.yaml");

        List<SpecFile> specFiles = openAPIFileParsingStrategy.parseResources(resources);

        assertNotNull(specFiles);
        assertFalse(specFiles.isEmpty());
        assertEquals("test.yaml", specFiles.getFirst().getFilename());
        assertNull(specFiles.getFirst().getLastCommitter());
    }

    @Test
    void parseResource_Empty() {
        List<Resource> resources = new ArrayList<>(Collections.singletonList(resourceMock));

        when(openAPIV3ParserMock.readContents(any(), any(), any(ParseOptions.class))).thenReturn(getParseResultYamlContent(""));
        when(resourceMock.getFilename()).thenReturn("test.yaml");

        List<SpecFile> specFiles = openAPIFileParsingStrategy.parseResources(resources);

        assertNotNull(specFiles);
        assertTrue(specFiles.isEmpty());
    }

    @Test
    void parseResource_NonOpenAPI() {
        List<Resource> resources = new ArrayList<>(Collections.singletonList(resourceMock));

        when(openAPIV3ParserMock.readContents(any(), any(), any(ParseOptions.class))).thenReturn(getParseResultYamlContent(getNonOpenAPIYamlContent()));
        when(resourceMock.getFilename()).thenReturn("test.yaml");

        List<SpecFile> specFiles = openAPIFileParsingStrategy.parseResources(resources);

        assertNotNull(specFiles);
        assertTrue(specFiles.isEmpty());
    }

    @Test
    void parseResource_withIOException() throws Exception {
        List<Resource> resources = new ArrayList<>(Collections.singletonList(resourceMock));

        when(resourceMock.getContentAsString(any())).thenThrow(new IOException());

        assertThrows(IOException.class, () -> openAPIFileParsingStrategy.parseResources(resources));
    }

    @Test
    void parseSpecFileInputs() {
        List<SpecFileInput> specFileInputs = new ArrayList<>(Collections.singletonList(specFileInputMock));

        when(openAPIV3ParserMock.readContents(any(), any(), any(ParseOptions.class))).thenReturn(getParseResultYamlContent(getOpenAPIYamlContent()));
        when(specFileInputMock.getContents()).thenReturn(inputStreamResourceMock);
        when(specFileInputMock.getFilePath()).thenReturn("test.yaml");
        when(specFileInputMock.getLastCommitter()).thenReturn("John Doe");

        List<SpecFile> specFiles = openAPIFileParsingStrategy.parseSpecFileInputs(specFileInputs);

        assertNotNull(specFiles);
        assertFalse(specFiles.isEmpty());
        assertEquals("test.yaml", specFiles.getFirst().getFilename());
        assertEquals("John Doe", specFiles.getFirst().getLastCommitter());
    }

    @Test
    void parseSpecFileInputs_Empty() {
        List<SpecFileInput> specFileInputs = new ArrayList<>(Collections.singletonList(specFileInputMock));

        when(openAPIV3ParserMock.readContents(any(), any(), any(ParseOptions.class))).thenReturn(getParseResultYamlContent(""));
        when(specFileInputMock.getContents()).thenReturn(inputStreamResourceMock);
        when(specFileInputMock.getFilePath()).thenReturn("test.yaml");

        List<SpecFile> specFiles = openAPIFileParsingStrategy.parseSpecFileInputs(specFileInputs);

        assertNotNull(specFiles);
        assertTrue(specFiles.isEmpty());
    }

    @Test
    void parseSpecFileInputs_NonOpenAPI() {
        List<SpecFileInput> specFileInputs = new ArrayList<>(Collections.singletonList(specFileInputMock));

        when(openAPIV3ParserMock.readContents(any(), any(), any(ParseOptions.class))).thenReturn(getParseResultYamlContent(getNonOpenAPIYamlContent()));
        when(specFileInputMock.getContents()).thenReturn(inputStreamResourceMock);
        when(specFileInputMock.getFilePath()).thenReturn("test.yaml");

        List<SpecFile> specFiles = openAPIFileParsingStrategy.parseSpecFileInputs(specFileInputs);

        assertNotNull(specFiles);
        assertTrue(specFiles.isEmpty());
    }

    @Test
    void parseSpecFileInputs_withIOException() throws Exception {
        List<SpecFileInput> specFileInputs = new ArrayList<>(Collections.singletonList(specFileInputMock));

        when(specFileInputMock.getContents()).thenReturn(inputStreamResourceMock);
        when(inputStreamResourceMock.getContentAsString(any())).thenThrow(new IOException());

        assertThrows(IOException.class, () -> openAPIFileParsingStrategy.parseSpecFileInputs(specFileInputs));
    }

    private SwaggerParseResult getParseResultYamlContent(final String yamlContent) {
        return new OpenAPIV3Parser().readContents(yamlContent);
    }

    private String getOpenAPIYamlContent() {
        return "openapi: \"3.0.3\"\ntestOpenAPI: \"testOpenAPIValue\"";
    }

    private String getNonOpenAPIYamlContent() {
        return "testNonOpenAPI: \"testNonOpenAPIValue\"";
    }

}