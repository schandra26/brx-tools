package com.broadridge.brx.matmapparser.persistence.adapters;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.model.mapping.persistence.MatmapMapping;
import com.broadridge.brx.matmapparser.persistence.adapter.MappingRepositoryAdapter;
import com.broadridge.brx.matmapparser.persistence.adapter.MatmapMappingRepositoryAdapter;
import com.broadridge.brx.matmapparser.persistence.repository.MatmapMappingRepository;
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
class MatmapMappingRepositoryAdapterTest {

    @Mock
    private MatmapMappingRepository matmapMappingRepository;

    private MappingRepositoryAdapter matmapMappingRepositoryAdapter;

    @Captor
    ArgumentCaptor<List<MatmapMapping>> matmapMappingCaptor;

    @BeforeEach
    void setup() {
        matmapMappingRepositoryAdapter = new MatmapMappingRepositoryAdapter(matmapMappingRepository);
    }

    @Test
    void findAll() {
        final List<MatmapMapping> matmapMappings = List.of(MatmapMapping.builder().filename("test1.matmap").sourceField("sourceField1").targetField("targetField1").build());

        when(matmapMappingRepository.findAll()).thenReturn(matmapMappings);

        final List<Mapping> result = matmapMappingRepositoryAdapter.findAll();

        assertEquals(1, result.size());
        assertEquals(matmapMappings.getFirst().getFilename(), result.getFirst().getFilename());
        assertEquals(matmapMappings.getFirst().getSourceField(), result.getFirst().getSourceField());
        assertEquals(matmapMappings.getFirst().getTargetField(), result.getFirst().getTargetField());

        verify(matmapMappingRepository).findAll();
    }

    @Test
    void saveAll() {
        final List<Mapping> mappings = List.of(Mapping.builder().filename("test1.matmap").sourceField("sourceField1").targetField("targetField1").build());

        matmapMappingRepositoryAdapter.saveAll(mappings);

        verify(matmapMappingRepository).saveAll(matmapMappingCaptor.capture());

        List<MatmapMapping> saved = matmapMappingCaptor.getValue();
        assertEquals(1, saved.size());
        assertEquals(mappings.getFirst().getFilename(), saved.getFirst().getFilename());
        assertEquals(mappings.getFirst().getSourceField(), saved.getFirst().getSourceField());
        assertEquals(mappings.getFirst().getTargetField(), saved.getFirst().getTargetField());
    }

    @Test
    void deleteAll() {
        matmapMappingRepositoryAdapter.deleteAll();

        verify(matmapMappingRepository).deleteAll();
    }
}
