package com.broadridge.brx.matmapparser.persistence.adapter;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.model.mapping.persistence.OpenAPIMapping;
import com.broadridge.brx.matmapparser.persistence.repository.OpenAPIMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("openAPIMapping")
@RequiredArgsConstructor
public class OpenAPIMappingRepositoryAdapter implements MappingRepositoryAdapter {

    final OpenAPIMappingRepository openAPIMappingRepository;

    @Override
    public List<Mapping> findAll() {
        return openAPIMappingRepository.findAll()
                .stream()
                .map(this::convertEntityToDomain)
                .toList();
    }

    @Override
    public void saveAll(List<Mapping> mappings) {
        openAPIMappingRepository.saveAll(mappings.stream()
                .map(this::convertDomainToEntity)
                .toList());
    }

    @Override
    public void deleteAll() {
        openAPIMappingRepository.deleteAll();
    }

    private Mapping convertEntityToDomain(final OpenAPIMapping openAPIMapping) {
        return Mapping.builder()
                .id(openAPIMapping.getId())
                .filename(openAPIMapping.getFilename())
                .sourceField(openAPIMapping.getSourceField())
                .targetField(openAPIMapping.getTargetField())
                .createdAt(openAPIMapping.getCreatedAt())
                .lastCommitter(openAPIMapping.getLastCommitter())
                .lastCommittedAt(openAPIMapping.getLastCommittedAt())
                .nonBrxEnumerationMetadata(openAPIMapping.getNonBrxEnumerationMetadata())
                .build();
    }

    private OpenAPIMapping convertDomainToEntity(final Mapping mapping) {
        return OpenAPIMapping.builder()
                .id(mapping.getId())
                .filename(mapping.getFilename())
                .sourceField(mapping.getSourceField())
                .targetField(mapping.getTargetField())
                .createdAt(mapping.getCreatedAt())
                .lastCommitter(mapping.getLastCommitter())
                .lastCommittedAt(mapping.getLastCommittedAt())
                .nonBrxEnumerationMetadata(mapping.getNonBrxEnumerationMetadata())
                .build();
    }
}
