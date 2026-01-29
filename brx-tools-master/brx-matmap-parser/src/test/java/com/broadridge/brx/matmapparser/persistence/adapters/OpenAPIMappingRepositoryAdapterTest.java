package com.broadridge.brx.matmapparser.persistence.adapters;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.model.mapping.persistence.OpenAPIMapping;
import com.broadridge.brx.matmapparser.persistence.adapter.MappingRepositoryAdapter;
import com.broadridge.brx.matmapparser.persistence.adapter.OpenAPIMappingRepositoryAdapter;
import com.broadridge.brx.matmapparser.persistence.repository.OpenAPIMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class OpenAPIMappingRepositoryAdapterTest {

    @Mock
    private OpenAPIMappingRepository openAPIMappingRepository;

    private MappingRepositoryAdapter openAPIMappingRepositoryAdapter;

    @Captor
    ArgumentCaptor<List<OpenAPIMapping>> openAPIMappingCaptor;

    @BeforeEach
    void setup() {
        openAPIMappingRepositoryAdapter = new OpenAPIMappingRepositoryAdapter(openAPIMappingRepository);
    }

    @Test
    void findAll() {
        final List<OpenAPIMapping> openAPIMappings = List.of(OpenAPIMapping.builder().filename("test1.yaml").sourceField("sourceField1").targetField("targetField1").build());

        when(openAPIMappingRepository.findAll()).thenReturn(openAPIMappings);

        final List<Mapping> result = openAPIMappingRepositoryAdapter.findAll();

        assertEquals(1, result.size());
        assertEquals(openAPIMappings.getFirst().getFilename(), result.getFirst().getFilename());
        assertEquals(openAPIMappings.getFirst().getSourceField(), result.getFirst().getSourceField());
        assertEquals(openAPIMappings.getFirst().getTargetField(), result.getFirst().getTargetField());

        verify(openAPIMappingRepository).findAll();
    }

    @Test
    void saveAll() {
        final List<Mapping> mappings = List.of(Mapping.builder().filename("test1.matmap").sourceField("sourceField1").targetField("targetField1").build());

        openAPIMappingRepositoryAdapter.saveAll(mappings);

        verify(openAPIMappingRepository).saveAll(openAPIMappingCaptor.capture());

        List<OpenAPIMapping> saved = openAPIMappingCaptor.getValue();
        assertEquals(1, saved.size());
        assertEquals(mappings.getFirst().getFilename(), saved.getFirst().getFilename());
        assertEquals(mappings.getFirst().getSourceField(), saved.getFirst().getSourceField());
        assertEquals(mappings.getFirst().getTargetField(), saved.getFirst().getTargetField());
    }

    @Test
    void deleteAll() {
        openAPIMappingRepositoryAdapter.deleteAll();

        verify(openAPIMappingRepository).deleteAll();
    }
}
