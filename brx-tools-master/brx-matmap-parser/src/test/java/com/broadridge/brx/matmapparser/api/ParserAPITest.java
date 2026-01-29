package com.broadridge.brx.matmapparser.api;

import com.broadridge.brx.matmapparser.model.StrategyContext;
import com.broadridge.brx.matmapparser.processor.file.FileProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ParserAPITest {

    @Mock
    private FileProcessor fileProcessorMock;

    @InjectMocks
    private ParserAPI parserAPI;

    @Test
    void parseDirectBrxMatmap_singleFile() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "matmapFiles",
                "test.matmap",
                "text/plain",
                "sample content".getBytes()
        );

        ResponseEntity<String> response = parserAPI.parseDirectBrxMatmap(mockFile);

        verify(fileProcessorMock, times(1)).processResources(anyList(), any(StrategyContext.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully scanned matmap files", response.getBody());
    }

    @Test
    void parseDirectBrxMatmap_multipleFiles() {
        MockMultipartFile mockFile1 = new MockMultipartFile(
                "matmapFiles",
                "test1.matmap",
                "text/plain",
                "sample content 1".getBytes()
        );

        MockMultipartFile mockFile2 = new MockMultipartFile(
                "matmapFiles",
                "test2.matmap",
                "text/plain",
                "sample content 2".getBytes()
        );

        ResponseEntity<String> response = parserAPI.parseDirectBrxMatmap(mockFile1, mockFile2);

        verify(fileProcessorMock, times(1)).processResources(anyList(), any(StrategyContext.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully scanned matmap files", response.getBody());
    }

    @Test
    void parseValidatedMatmap() {
        MockMultipartFile mockFile1 = new MockMultipartFile(
                "matmapFiles",
                "test1.matmap",
                "text/plain",
                "sample content 1".getBytes()
        );

        ResponseEntity<String> response = parserAPI.parseValidatedMatmap(mockFile1);

        verify(fileProcessorMock, times(1)).processResources(anyList(), any(StrategyContext.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully scanned validated matmap file", response.getBody());
    }

    @Test
    void parseOpenAPISpec() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "openAPISpecFiles",
                "testOpenAPI.yaml",
                "text/plain",
                "sample content".getBytes()
        );
        ResponseEntity<String> response = parserAPI.parseOpenAPISpec(mockMultipartFile);

        verify(fileProcessorMock, times(1)).processResources(anyList(), any(StrategyContext.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully scanned OpenAPI files", response.getBody());
    }
}