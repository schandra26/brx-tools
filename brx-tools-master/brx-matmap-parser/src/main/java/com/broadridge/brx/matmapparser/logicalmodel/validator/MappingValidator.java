package com.broadridge.brx.matmapparser.logicalmodel.validator;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;

public interface MappingValidator {

    boolean isValid(final Mapping mapping);

    boolean isBRxDataModel(String modelName);

    boolean isAPIModelFromLookupTable(String apiModelName);

    boolean containsBRxDataAttribute(String matmapValue);

    boolean isBRxShortenedLabelName(String modelName, String entityName, String propertyName);

    boolean isBRxShortenedEntityName(String modelName, String entityName);
}
