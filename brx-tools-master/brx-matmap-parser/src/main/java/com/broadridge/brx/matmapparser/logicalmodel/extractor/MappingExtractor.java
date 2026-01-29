package com.broadridge.brx.matmapparser.logicalmodel.extractor;

import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModel;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModelExcelRow;

import java.util.List;

public interface MappingExtractor {

    BRxDataModel getBRxDataModel(String modelName);

    List<BRxDataModel> getBRxDataModelsFromLookupTable(final String modelName);

    /** Method that extracts the brx property rows that will be used for the target of the brx mapping (for both root and
     * nested properties in the open api specs)
     *
     * @param parentSchemaNames list of schema name path from root to the property
     * @param propertyName the name of the property
     * @param brxModels the brx models in which are of interest to find the brx mapping
     * @return the list of brx data rows that were extracted
     */
    List<BRxDataModelExcelRow> extractBRxTargets(final List<String> parentSchemaNames, final String propertyName, final List<BRxDataModel> brxModels);

    /** Extracts and builds the string that will be used as the target value for the mapping
     *
     * @param targets the target brx data rows to be used for building the target string
     * @param propertyName the property name for which the mapping target is built
     * @param parentSchemaNames list of schema name path from root to the property
     * @return the string that will be used as the target part of the mapping
     */
    String extractMappingTargetString(final List<BRxDataModelExcelRow> targets, final String propertyName, final List<String> parentSchemaNames);

    /** Extracts and builds the string that lists all the enum values of a property from open api spec that were not found in brx
     *
     * @param targets the target brx data rows to be used for building the target string
     * @param propertyName the property name for which the mapping target is built
     * @param parentSchemaNames list of schema name path from root to the property
     * @param apiEnumValues list of the enum values of the property coming from the openapi spec
     * @return a string of enum values from openapi spec that were not found in brx
     */
    String extractNonBRxEnumValuesString(final List<BRxDataModelExcelRow> targets, final String propertyName, final List<String> parentSchemaNames, final List<String> apiEnumValues);
}
