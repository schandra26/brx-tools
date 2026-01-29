package com.broadridge.brx.matmapparser.service;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.persistence.adapter.MappingRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MappingQueryServiceTest {

    @Mock
    private MappingRepositoryAdapter mappingRepositoryMock;

    private MappingQueryService mappingQueryService;

    @BeforeEach
    void setUp() {
        Map<String, MappingRepositoryAdapter> mappingRepositories = new HashMap<>();
        mappingRepositories.put("openAPIType", mappingRepositoryMock);

        mappingQueryService = new MappingQueryService(mappingRepositories);
    }

    @Test
    void getMappings_all() {
        List<Mapping> testMappings = getTestMappings();
        when(mappingRepositoryMock.findAll()).thenReturn(testMappings);

        List<Mapping> mappings = mappingQueryService.getFilteredMappings(null, null, null);

        assertNotNull(mappings);
        assertEquals(2, mappings.size());
        assertEquals("test1.yaml", mappings.getFirst().getFilename());
        assertEquals("sourceField1", mappings.getFirst().getSourceField());
        assertEquals("targetField1", mappings.getFirst().getTargetField());
        assertEquals("test2.yaml", mappings.get(1).getFilename());
        assertEquals("sourceField2", mappings.get(1).getSourceField());
        assertEquals("targetField2", mappings.get(1).getTargetField());
    }

    @Test
    void getMappings_filenameFilter() {
        List<Mapping> testMappings = getTestMappings();
        when(mappingRepositoryMock.findAll()).thenReturn(testMappings);

        List<Mapping> mappings = mappingQueryService.getFilteredMappings("test2.yaml", "", "");

        assertNotNull(mappings);
        assertEquals(1, mappings.size());
        assertEquals("test2.yaml", mappings.getFirst().getFilename());
        assertEquals("sourceField2", mappings.getFirst().getSourceField());
        assertEquals("targetField2", mappings.getFirst().getTargetField());
    }

    @Test
    void getMappings_sourceFilter() {
        List<Mapping> testMappings = getTestMappings();
        when(mappingRepositoryMock.findAll()).thenReturn(testMappings);

        List<Mapping> mappings = mappingQueryService.getFilteredMappings("", "sourceField2", "");

        assertNotNull(mappings);
        assertEquals(1, mappings.size());
        assertEquals("test2.yaml", mappings.getFirst().getFilename());
        assertEquals("sourceField2", mappings.getFirst().getSourceField());
        assertEquals("targetField2", mappings.getFirst().getTargetField());
    }

    @Test
    void getMappings_targetFilter() {
        List<Mapping> testMappings = getTestMappings();
        when(mappingRepositoryMock.findAll()).thenReturn(testMappings);

        List<Mapping> mappings = mappingQueryService.getFilteredMappings("", "", "targetField1");

        assertNotNull(mappings);
        assertEquals(1, mappings.size());
        assertEquals("test1.yaml", mappings.getFirst().getFilename());
        assertEquals("sourceField1", mappings.getFirst().getSourceField());
        assertEquals("targetField1", mappings.getFirst().getTargetField());
    }

    @Test
    void getMappings_emptyFindAll() {
        when(mappingRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        List<Mapping> mappings = mappingQueryService.getFilteredMappings(null, null, null);

        assertNotNull(mappings);
        assertTrue(mappings.isEmpty());
    }

    @Test
    void getMappingExcel() {
        List<Mapping> testMappings = getTestMappings();
        when(mappingRepositoryMock.findAll()).thenReturn(testMappings);

        Resource resource = mappingQueryService.getMappingsExcel();

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    private List<Mapping> getTestMappings() {
        Mapping mappingOne = Mapping.builder()
                .filename("test1.yaml")
                .sourceField("sourceField1")
                .targetField("targetField1")
                .lastCommittedAt(Instant.now())
                .createdAt(Instant.now())
                .build();
        Mapping mappingTwo = Mapping.builder()
                .filename("test2.yaml")
                .sourceField("sourceField2")
                .targetField("targetField2")
                .lastCommittedAt(Instant.now())
                .createdAt(Instant.now())
                .build();

        return List.of(mappingOne, mappingTwo);
    }
}