package com.broadridge.brx.matmapparser.service;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.persistence.adapter.MappingRepositoryAdapter;
import com.broadridge.brx.matmapparser.util.MappingsExcelCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MappingQueryService implements QueryService {

    private final Map<String, MappingRepositoryAdapter> mappingRepositories;

    @Override
    public List<Mapping> getFilteredMappings(String filename, String source, String target) {
        if (Objects.isNull(filename) && Objects.isNull(source) && Objects.isNull(target)) {
            return getAllMappings();
        }

        return getAllMappings().stream()
                .filter(mapping -> filterByFilename(mapping, filename))
                .filter(mapping -> filterBySource(mapping, source))
                .filter(mapping -> filterByTarget(mapping, target))
                .toList();
    }

    @Override
    public Resource getMappingsExcel() {
        List<Mapping> mappings = getAllMappings();
        return MappingsExcelCreator.getMappingsExcelResource(mappings);
    }

    private List<Mapping> getAllMappings() {
        return mappingRepositories.values().stream()
                .map(MappingRepositoryAdapter::findAll)
                .flatMap(List::stream)
                .toList();
    }

    private boolean filterByFilename(Mapping mapping, String filename) {
        if (filename.isBlank()) {
            return true;
        }
        return mapping.getFilename().toLowerCase().contains(filename.toLowerCase());
    }

    private boolean filterBySource(Mapping mapping, String sourceFilter) {
        if (sourceFilter.isBlank()) {
            return true;
        }
        return mapping.getSourceField().toLowerCase().contains(sourceFilter.toLowerCase());
    }

    private boolean filterByTarget(Mapping mapping, String targetFilter) {
        if (targetFilter.isBlank()) {
            return true;
        }
        if (mapping.getTargetField() == null) {
            return false;
        }
        return mapping.getTargetField().toLowerCase().contains(targetFilter.toLowerCase());
    }
}
