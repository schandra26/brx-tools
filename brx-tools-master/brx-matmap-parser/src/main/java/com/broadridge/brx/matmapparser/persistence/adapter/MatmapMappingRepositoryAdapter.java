package com.broadridge.brx.matmapparser.persistence.adapter;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.model.mapping.persistence.MatmapMapping;
import com.broadridge.brx.matmapparser.persistence.repository.MatmapMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("matmapMapping")
@RequiredArgsConstructor
public class MatmapMappingRepositoryAdapter implements MappingRepositoryAdapter {

    final MatmapMappingRepository matmapMappingRepository;

    @Override
    public List<Mapping> findAll() {
        return matmapMappingRepository.findAll()
                .stream()
                .map(this::convertEntityToDomain)
                .toList();
    }

    @Override
    public void saveAll(List<Mapping> mappings) {
        matmapMappingRepository.saveAll(mappings.stream()
                .map(this::convertDomainToEntity)
                .toList());
    }

    @Override
    public void deleteAll() {
        matmapMappingRepository.deleteAll();
    }

    private Mapping convertEntityToDomain(final MatmapMapping matmapMapping) {
        return Mapping.builder()
                .id(matmapMapping.getId())
                .filename(matmapMapping.getFilename())
                .sourceField(matmapMapping.getSourceField())
                .targetField(matmapMapping.getTargetField())
                .createdAt(matmapMapping.getCreatedAt())
                .lastCommitter(matmapMapping.getLastCommitter())
                .lastCommittedAt(matmapMapping.getLastCommittedAt())
                .nonBrxEnumerationMetadata(matmapMapping.getNonBrxEnumerationMetadata())
                .build();
    }

    private MatmapMapping convertDomainToEntity(final Mapping matmapMapping) {
        return MatmapMapping.builder()
                .id(matmapMapping.getId())
                .filename(matmapMapping.getFilename())
                .sourceField(matmapMapping.getSourceField())
                .targetField(matmapMapping.getTargetField())
                .createdAt(matmapMapping.getCreatedAt())
                .lastCommitter(matmapMapping.getLastCommitter())
                .lastCommittedAt(matmapMapping.getLastCommittedAt())
                .nonBrxEnumerationMetadata(matmapMapping.getNonBrxEnumerationMetadata())
                .build();
    }
}
