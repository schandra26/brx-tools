package com.broadridge.brx.matmapparser.logicalmodel.validator;

import com.broadridge.brx.matmapparser.logicalmodel.extractor.MappingExtractor;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModel;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModelExcelRow;
import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.parsers.ExcelParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.broadridge.brx.matmapparser.util.StringUtil.containsAllChars;

@Service
@RequiredArgsConstructor
@Slf4j
public class BRxLogicalModelMappingValidator implements MappingValidator {

    private final ExcelParser excelParser;
    private final MappingExtractor mappingExtractor;

    @Override
    public boolean isValid(final Mapping mapping) {
        if (mapping.getTargetField() == null || mapping.getTargetField().isEmpty()) {
            return false;
        }
        final BRxDataModel brxDataModel = getBRxDataModel(mapping);
        if (brxDataModel != null) {
            for (final BRxDataModelExcelRow brxDataModelExcelRow : brxDataModel.records()) {
                final String shortenedLabelName = brxDataModelExcelRow.shortenedLabelName();
                final String dataAttribute = brxDataModelExcelRow.dataAttribute();
                boolean shortenedLabelNameMatch = shortenedLabelName != null && !shortenedLabelName.isEmpty() &&
                        (containsAllChars(mapping.getTargetField().toLowerCase(), brxDataModelExcelRow.shortenedLabelName().toLowerCase()) ||
                                containsAllChars(brxDataModelExcelRow.shortenedLabelName().toLowerCase(), mapping.getTargetField().toLowerCase()));
                boolean dataAttributeMatch = dataAttribute != null && !dataAttribute.isEmpty() &&
                        (containsAllChars(mapping.getTargetField().toLowerCase(), brxDataModelExcelRow.dataAttribute().toLowerCase()) ||
                                containsAllChars(brxDataModelExcelRow.dataAttribute().toLowerCase(), mapping.getTargetField().toLowerCase()));
                if (shortenedLabelNameMatch && dataAttributeMatch) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isBRxDataModel(String modelName) {
        return mappingExtractor.getBRxDataModel(modelName) != null;
    }

    @Override
    public boolean isAPIModelFromLookupTable(String modelName) {
        return !mappingExtractor.getBRxDataModelsFromLookupTable(modelName).isEmpty();
    }

    @Override
    public boolean containsBRxDataAttribute(final String matmapValue) {
        List<BRxDataModel> brxDataModels = excelParser.getBrxDataModels();

        for (BRxDataModel brxDataModel : brxDataModels) {
            for (BRxDataModelExcelRow brxDataModelExcelRow : brxDataModel.records()) {
                if (matmapValue.contains(brxDataModelExcelRow.dataAttribute()) && !brxDataModelExcelRow.dataAttribute().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isBRxShortenedLabelName(String modelName, String entityName, String propertyName) {
        List<BRxDataModel> brxDataModels = excelParser.getBrxDataModels();

        for (BRxDataModel brxDataModel : brxDataModels) {
            if (brxDataModel.name().equals(modelName)) {
                for (BRxDataModelExcelRow brxDataModelExcelRow : brxDataModel.records()) {
                    if (brxDataModelExcelRow.shortenedLabelName().equalsIgnoreCase(propertyName) &&
                            (brxDataModelExcelRow.shortenedEntityName().equalsIgnoreCase(entityName)) || brxDataModelExcelRow.dataEntity().equalsIgnoreCase(entityName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isBRxShortenedEntityName(String modelName, String entityName) {
        List<BRxDataModel> brxDataModels = excelParser.getBrxDataModels();

        for (BRxDataModel brxDataModel : brxDataModels) {
            if (brxDataModel.name().equals(modelName)) {
                for (BRxDataModelExcelRow brxDataModelExcelRow : brxDataModel.records()) {
                    if (entityName.equals(brxDataModelExcelRow.shortenedEntityName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private BRxDataModel getBRxDataModel(final Mapping outputElement) {
        for (final BRxDataModel brxDataModel : excelParser.getBrxDataModels()) {
            if (outputElement.getEntity().contains(brxDataModel.name())) {
                return brxDataModel;
            }
        }
        return null;
    }
}
