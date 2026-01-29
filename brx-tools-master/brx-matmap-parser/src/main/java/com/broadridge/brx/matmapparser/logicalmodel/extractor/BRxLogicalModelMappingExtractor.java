package com.broadridge.brx.matmapparser.logicalmodel.extractor;

import com.broadridge.brx.matmapparser.logicalmodel.enumeration.BRxEnumerationMetadataExcelRow;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModel;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModelExcelRow;
import com.broadridge.brx.matmapparser.parsers.ExcelParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BRxLogicalModelMappingExtractor implements MappingExtractor {

    //dev-note : mocked lookup table to api models that use brx model properties and are not named as the brx models
    private final Map<String, List<String>> apiModelsToBRxModelsLookupTable = Map.ofEntries(
            Map.entry("account", List.of("party")),
            Map.entry("settlementinstruction", List.of("party")),
            Map.entry("profile", List.of("party")),
            Map.entry("trade", List.of("transaction")),
            Map.entry("moneymovement", List.of("transaction", "party", "instrument")),
            Map.entry("transfer", List.of("transaction", "party", "instrument"))
    );

    //dev-note : list of objects/arrays of objects that were flattened for some api models
    private final List<String> possiblyFlattenedEntityShortLabelNames = List.of("flags", "classifications", "dates", "identifiers", "narratives");

    private final ExcelParser excelParser;

    @Override
    public BRxDataModel getBRxDataModel(String modelName) {
        List<BRxDataModel> brxDataModels = excelParser.getBrxDataModels();

        for (BRxDataModel brxDataModel : brxDataModels) {
            if (modelName.equalsIgnoreCase(brxDataModel.name())) {
                return brxDataModel;
            }
        }
        return null;
    }

    @Override
    public List<BRxDataModel> getBRxDataModelsFromLookupTable(String modelName) {
        if (modelName == null) {
            return Collections.emptyList();
        }
        return apiModelsToBRxModelsLookupTable.getOrDefault(modelName.toLowerCase(), Collections.emptyList())
                .stream()
                .map(this::getBRxDataModel)
                .toList();
    }

    @Override
    public List<BRxDataModelExcelRow> extractBRxTargets(List<String> parentSchemaNames, String propertyName, List<BRxDataModel> brxModels) {
        if (brxModels == null) {
            return List.of();
        }

        Map<String, List<BRxDataModelExcelRow>> rowsAssociatedToParentSchemaNames = extractRowsAssociatedToParentSchemaNames(brxModels, parentSchemaNames);

        filterRowsMatchingOnPropertyName(rowsAssociatedToParentSchemaNames, propertyName);
        cleanupEmptyEntriesAndDuplicateRows(rowsAssociatedToParentSchemaNames);

        List<BRxDataModelExcelRow> rowsMatchingOnLatestParentSchemaName = extractRowsMatchingOnLatestParentSchemaName(rowsAssociatedToParentSchemaNames, parentSchemaNames);
        if (!rowsMatchingOnLatestParentSchemaName.isEmpty()) {
            // dev-note : if multiple rows were found but the property is not at the root (it has multiple parents), then try to filter out dependent rows, as in this case the property is more likely to be a non-dependent property
            if (rowsMatchingOnLatestParentSchemaName.size() > 1 && parentSchemaNames.size() == 1) {
                return extractNonDependentRowFromList(rowsMatchingOnLatestParentSchemaName, propertyName);
            }
            return rowsMatchingOnLatestParentSchemaName;
        }

        // dev-note : if no rows were found matching on the latest parent schema name, then try to find rows that match on possibly flattened entities (note that this will return all the rows found for the flattened entity)
        List<BRxDataModelExcelRow> possiblyFlattenedRows = extractPossiblyFlattenedRows(extractRowsAssociatedToParentSchemaNames(brxModels, parentSchemaNames), parentSchemaNames);
        if (!possiblyFlattenedRows.isEmpty()) {
            return possiblyFlattenedRows;
        }

        return List.of();
    }

    @Override
    public String extractMappingTargetString(List<BRxDataModelExcelRow> targets, String propertyName, List<String> parentSchemaNames) {
        String simpleMappingTargetString = extractSimpleMappingTargetString(targets);
        if (simpleMappingTargetString != null) {
            return simpleMappingTargetString;
        }
        return extractComplexTargetString(targets, propertyName, parentSchemaNames);
    }

    @Override
    public String extractNonBRxEnumValuesString(List<BRxDataModelExcelRow> targets, String propertyName, List<String> parentSchemaNames, List<String> apiEnumValues) {
        String simpleNonBRxEnumValuesString = extractSimpleNonBRxEnumValuesString(targets, apiEnumValues);
        if (simpleNonBRxEnumValuesString != null) {
            return simpleNonBRxEnumValuesString;
        }
        return extractComplexNonBRxEnumValuesString(targets, propertyName, parentSchemaNames, apiEnumValues);
    }

    /** Method that extracts all the brx data rows that are associated to any of the parent schema names
     *
     * @param brxModels set of brx models in which to search for the rows
     * @param parentSchemaNames list of schema name path from root to the property
     * @return a map of parent schema name to the list of brx data rows that were identified
     */
    private Map<String, List<BRxDataModelExcelRow>> extractRowsAssociatedToParentSchemaNames(List<BRxDataModel> brxModels, List<String> parentSchemaNames) {
        Map<String, List<BRxDataModelExcelRow>> rowsAssociatedToParentSchemaNames = new LinkedHashMap<>();
        List<BRxDataModelExcelRow> allRows = brxModels.stream().map(BRxDataModel::records).flatMap(Collection::stream).toList();
        for (String parentSchemaName : parentSchemaNames) {
            for (BRxDataModelExcelRow row : allRows) {
                if (isRowAssociatedToSchemaName(row, parentSchemaName)) {
                    rowsAssociatedToParentSchemaNames.computeIfAbsent(parentSchemaName, k -> new ArrayList<>()).add(row);
                }
            }
        }
        return rowsAssociatedToParentSchemaNames;
    }

    /**Method that checks if a brx data row is associated to a schema name based on various matching criteria
     *
     * @param row the brx data row to check
     * @param parentSchemaName the schema name to check against
     * @return true or false
     */
    private boolean isRowAssociatedToSchemaName(BRxDataModelExcelRow row, String parentSchemaName) {
        return parentSchemaName.toLowerCase().contains(row.shortenedEntityName().toLowerCase()) ||
                row.shortenedEntityName().toLowerCase().contains(parentSchemaName.toLowerCase()) ||
                row.shortenedLabelName().toLowerCase().contains(parentSchemaName.toLowerCase()) ||
                parentSchemaName.toLowerCase().contains(row.dataModel().toLowerCase()) ||
                isParentSchemaNameKeyInLookupTable(parentSchemaName);
    }

    /** Method that filters the rows associated to schema names based on the property name
     *
     * @param rowsAssociatedToParentSchemaNames map of parent schema name to the list of brx data rows that were identified
     * @param propertyName the property name to use for the filtering
     */
    private void filterRowsMatchingOnPropertyName(Map<String, List<BRxDataModelExcelRow>> rowsAssociatedToParentSchemaNames, String propertyName) {
        rowsAssociatedToParentSchemaNames.values().forEach(bRxDataModelExcelRows ->  bRxDataModelExcelRows.removeIf(bRxDataModelExcelRow -> !bRxDataModelExcelRow.shortenedLabelName().toLowerCase().contains(propertyName.toLowerCase())));
    }

    /** Method that cleans up the map of rows associated to schema names by removing empty entries and entries that are subsets of other entries
     *
     * @param rowsAssociatedToParentSchemaNames map of parent schema name to the list of brx data rows that were identified
     */
    private void cleanupEmptyEntriesAndDuplicateRows(Map<String, List<BRxDataModelExcelRow>> rowsAssociatedToParentSchemaNames) {
        rowsAssociatedToParentSchemaNames.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        Set<Map.Entry<String, List<BRxDataModelExcelRow>>> entriesToRemove = rowsAssociatedToParentSchemaNames.entrySet().stream()
                .filter(entry1 -> rowsAssociatedToParentSchemaNames.entrySet().stream()
                        .anyMatch(entry2 -> isEntryDuplicate(entry1, entry2)))
                .collect(Collectors.toSet());
        rowsAssociatedToParentSchemaNames.entrySet().removeAll(entriesToRemove);
    }

    /** Method that checks if two entries in the map of rows associated to schema names are duplicates (one is a subset of the other)
     *
     * @param entry1 the first entry to check
     * @param entry2 the second entry to check
     * @return true or false
     */
    private boolean isEntryDuplicate(Map.Entry<String, List<BRxDataModelExcelRow>> entry1, Map.Entry<String, List<BRxDataModelExcelRow>> entry2) {
        return !entry1.getKey().equals(entry2.getKey()) && entry1.getKey().contains(entry2.getKey()) && new HashSet<>(entry1.getValue()).equals(new HashSet<>(entry2.getValue()));
    }

    /** Method that extracts the rows that match based on the latest parent schema name in the hierarchy
     *
     * @param rowsAssociatedToParentSchemaNames map of parent schema name to the list of brx data rows that were identified
     * @param parentSchemaNames list of schema name path from root to the property
     * @return the list of brx data rows that were identified
     */
    private List<BRxDataModelExcelRow> extractRowsMatchingOnLatestParentSchemaName(Map<String, List<BRxDataModelExcelRow>> rowsAssociatedToParentSchemaNames, List<String> parentSchemaNames)
    {
        for (int i = parentSchemaNames.size() - 1; i >= 0; i--) {
            if (rowsAssociatedToParentSchemaNames.containsKey(parentSchemaNames.get(i))) {
                return extractRowsWithDataModelMatchingOnLatestParentSchemaName(rowsAssociatedToParentSchemaNames.get(parentSchemaNames.get(i)), parentSchemaNames);
            }
        }
        return List.of();
    }

    /** Method that extracts the rows that match based on the latest parent schema name in the hierarchy and also match on the data model name
     *
     * @param rows the list of brx data rows to check against
     * @param parentSchemaNames list of schema name path from root to the property
     * @return the list of brx data rows that were identified
     */
    private List<BRxDataModelExcelRow> extractRowsWithDataModelMatchingOnLatestParentSchemaName(List<BRxDataModelExcelRow> rows, List<String> parentSchemaNames) {
        List<BRxDataModelExcelRow> rowsMatchingOnLatestParentSchemaName = new ArrayList<>();
        for (int i = parentSchemaNames.size() - 1; i >= 0; i--) {
            for (BRxDataModelExcelRow row : rows) {
                if (parentSchemaNames.get(i).toLowerCase().contains(row.dataModel().toLowerCase()) || isParentSchemaNameKeyInLookupTable(parentSchemaNames.get(i))) {
                    rowsMatchingOnLatestParentSchemaName.add(row);
                }
            }
        }
        return rowsMatchingOnLatestParentSchemaName;
    }

    /** Method that extracts the rows that match based on the possibly flattened entities list
     *
     * @param rowsAssociatedToParentSchemaNames map of parent schema name to the list of brx data rows that were identified
     * @param parentSchemaNames list of schema name path from root to the property
     * @return the list of brx data rows that were identified
     */
    private List<BRxDataModelExcelRow> extractPossiblyFlattenedRows(Map<String, List<BRxDataModelExcelRow>> rowsAssociatedToParentSchemaNames, List<String> parentSchemaNames) {
        List<BRxDataModelExcelRow> flattenedRows = new ArrayList<>();
        for (String flattenedShortenedLabelName : possiblyFlattenedEntityShortLabelNames) {
            if (rowsAssociatedToParentSchemaNames.containsKey(flattenedShortenedLabelName)) {
                flattenedRows.addAll(extractRowsWithDataModelMatchingOnLatestParentSchemaName(rowsAssociatedToParentSchemaNames.get(flattenedShortenedLabelName), parentSchemaNames));
            }
        }
        return flattenedRows;
    }

    /** Method that extracts from a list of rows, the non dependent rows that match the property name on shortened label name
     *
     * @param rows the list of brx data rows to check against
     * @param propertyName the property name to check
     * @return the list of brx data rows that were identified
     */
    private List<BRxDataModelExcelRow> extractNonDependentRowFromList(List<BRxDataModelExcelRow> rows, String propertyName) {
        List<BRxDataModelExcelRow> nonDependentRows = new ArrayList<>();
        for (BRxDataModelExcelRow row : rows) {
            if (!row.dependent()) {
                nonDependentRows.add(row);
            }
        }
        for (BRxDataModelExcelRow row : nonDependentRows) {
            if (isPropertyNameAShortenedLabelName(propertyName, row)) {
                return List.of(row);
            }
        }
        if (!nonDependentRows.isEmpty()) {
            return nonDependentRows;
        }
        return rows;
    }

    /** Method that checks for a match a parent schema name and key in the lookup table (used for cases where the api model name is different than the brx model name)
     *
     * @param parentSchemaName the schema name to check
     * @return true or false
     */
    private boolean isParentSchemaNameKeyInLookupTable(String parentSchemaName) {
        for (String lookupTableKey : apiModelsToBRxModelsLookupTable.keySet()) {
            if (parentSchemaName.toLowerCase().contains(lookupTableKey)) {
                return true;
            }
        }
        return false;
    }

    /** Method to find all the brx data rows that are found for a schema name (based on the shortenedEntityName of the brx data row)
     *
     * @param bRxDataRows the brx data rows from which to extract the rows that match by entity name
     * @param schemaName list of schema name to use for the search by entity name
     * @return the brx data rows that were identified
     */
    private List<BRxDataModelExcelRow> findBRxRowsByEntityName(List<BRxDataModelExcelRow> bRxDataRows, String schemaName) {
        List<BRxDataModelExcelRow> results = new ArrayList<>();
        for (BRxDataModelExcelRow row : bRxDataRows) {
            if (row.shortenedEntityName().toLowerCase().contains(schemaName.toLowerCase())) {
                results.add(row);
            }
        }
        return results;
    }

    /** Method that extracts and builds the mapping target string for the case where only one target data row was found
     *
     * @param targets the targeted brx data rows
     * @return a string that will be used as the target part of the mapping
     */
    private String extractSimpleMappingTargetString(final List<BRxDataModelExcelRow> targets) {
        if (targets.size() == 1) {
            return buildMappingTargetString(targets.getFirst().dataModel(), targets.getFirst().dataEntity(), targets.getFirst().dataAttribute());
        }
        return null;
    }

    /**
     *
     * @param targets the targeted brx data rows
     * @param propertyName the api property name for which the target string is extracted and built
     * @param parentSchemaNames list of schema name path from root to the property
     * @return a string that will be used as the target part of the mapping
     */
    private String extractComplexTargetString(final List<BRxDataModelExcelRow> targets, final String propertyName, final List<String> parentSchemaNames) {
        // dev-note : 1st step : firstly, try to find the matching target row, for cases where multiple rows were found for the same property name,
        // in other words for properties that are present across multiple models (examples for this are identifiers, dates, classifications, etc)
        // to do this we try to find both the model name and entity name within the parent schemas as well
        BRxDataModelExcelRow targetFromParentSchemaNamesAndModelName = extractTargetUsingParentSchemaNamesAndModelName(parentSchemaNames, targets, propertyName);
        if (targetFromParentSchemaNamesAndModelName != null) {
            return buildMappingTargetString(targetFromParentSchemaNamesAndModelName.dataModel(), targetFromParentSchemaNamesAndModelName.dataEntity(), targetFromParentSchemaNamesAndModelName.dataAttribute());
        }
        // dev-note : 2nd step : if no match was found by matching both entity name and model name, then try to match only by entity name (for simpler cases where a check for entity name in the parents is enough)
        BRxDataModelExcelRow targetFromParentSchemaNames = extractTargetUsingParentSchemaNames(parentSchemaNames, targets, propertyName);
        if (targetFromParentSchemaNames != null) {
            return buildMappingTargetString(targetFromParentSchemaNames.dataModel(), targetFromParentSchemaNames.dataEntity(), targetFromParentSchemaNames.dataAttribute());
        }
        // dev-note : if no rows that match with the property name were found, we attempt to treat the flattened case
        final String possiblyFlattenedSchemaName = getPossiblyFlattenedSchemaNameFromParentSchemaNames(parentSchemaNames);
        if (!targets.isEmpty() && possiblyFlattenedSchemaName != null) {
            List<BRxDataModelExcelRow> flattenedTargets = findBRxRowsByEntityName(targets, possiblyFlattenedSchemaName);
            if (!flattenedTargets.isEmpty()) {
                return buildMappingTargetString(flattenedTargets.getFirst().dataModel(), flattenedTargets.getFirst().dataEntity(), null);
            }
        }
        return null;
    }

    /** Method that extracts a single target brx data row from a list targets matching shortened entity name on one of parent schema names and property name matching on shortened label name
     *
     * @param parentSchemaNames list of schema name path from root to the property
     * @param targets the targeted brx data rows
     * @param propertyName the api property name for which the target row is extracted
     * @return the brx data row that was identified or null if none was found
     */
    private BRxDataModelExcelRow extractTargetUsingParentSchemaNames(List<String> parentSchemaNames, List<BRxDataModelExcelRow> targets, String propertyName) {
        for (int i = parentSchemaNames.size() - 1; i >= 0; i--) {
            for (final BRxDataModelExcelRow target : targets) {
                if (isPropertyNameAShortenedLabelName(propertyName, target) && parentSchemaNames.get(i).equalsIgnoreCase(target.shortenedEntityName())) {
                    return target;
                }
            }
        }
        return null;
    }

    /** Method that extracts a single target brx data row from a list targets matching both shortened entity name and data model name on one of parent schema names and property name matching on shortened label name
     * (for the cases where the row of an entity is found in multiple models, so we need to identify the model as well)
     *
     * @param parentSchemaNames list of schema name path from root to the property
     * @param targets the targeted brx data rows
     * @param propertyName the api property name for which the target row is extracted
     * @return the brx data row that was identified or null if none was found
     */
    private BRxDataModelExcelRow extractTargetUsingParentSchemaNamesAndModelName(List<String> parentSchemaNames, List<BRxDataModelExcelRow> targets, String propertyName) {
        boolean matchByEntityName = false;
        boolean matchByDataModelName = false;
        for (BRxDataModelExcelRow target : targets) {
            for (String parentSchemaName : parentSchemaNames) {
                if (target.shortenedEntityName().toLowerCase().contains(parentSchemaName.toLowerCase())) {
                    matchByEntityName = true;
                }
                if (parentSchemaName.toLowerCase().contains(target.dataModel().toLowerCase())) {
                    matchByDataModelName = true;
                }
            }
            if (matchByEntityName && matchByDataModelName && isPropertyNameAShortenedLabelName(propertyName, target)) {
                return target;
            }
        }
        return null;
    }

    /** Method that builds the actual target string of the mapping result (for flattened entities, it will only contain model and entity)
     *
     * @param model the model name of the mapping target string
     * @param entity the entity name of the mapping target string
     * @param attribute the entity name of the mapping target string
     * @return the built mapping target string
     */
    private String buildMappingTargetString(final String model, final String entity, final String attribute) {
        final String baseMappingString = model + "." + entity;
        if (attribute == null) {
            return baseMappingString;
        }
        return baseMappingString + " -> " + attribute;
    }

    /** Method that extracts builds the non brx values part of the mapping for the simple case where only one target brx data row was identified
     *
     * @param targets the targeted brx data rows
     * @param apiEnumValues the enum values that come from the open api spec
     * @return a string containing all the enum values that were not found in brx
     */
    private String extractSimpleNonBRxEnumValuesString(final List<BRxDataModelExcelRow> targets, final List<String> apiEnumValues) {
        if (targets.size() == 1) {
            return buildSimpleNonBRxEnumValuesString(targets.getFirst(), apiEnumValues);
        }
        return null;
    }

    /** Method that extracts and builds the non brx values part of the mapping for the complex case where multiple target brx data rows were identified
     *
     * @param targets the targeted brx data rows
     * @param propertyName the api property name for which the non brx enum values string is extracted and built
     * @param parentSchemaNames list of schema name path from root to the property
     * @param apiEnumValues the enum values that come from the open api spec
     * @return a string containing all the enum values that were not found in brx
     */
    private String extractComplexNonBRxEnumValuesString(final List<BRxDataModelExcelRow> targets, final String propertyName, final List<String> parentSchemaNames, final List<String> apiEnumValues) {
        // dev-note : 1st step : firstly, try to find the matching target row, for cases where multiple rows were found for the same property name,
        // in other words for properties that are present across multiple models (examples for this are identifiers, dates, classifications, etc)
        // to do this we try to find both the model name and entity name within the parent schemas as well
        BRxDataModelExcelRow targetFromParentSchemaNamesAndModelName = extractTargetUsingParentSchemaNamesAndModelName(parentSchemaNames, targets, propertyName);
        if (targetFromParentSchemaNamesAndModelName != null) {
            return buildSimpleNonBRxEnumValuesString(targetFromParentSchemaNamesAndModelName, apiEnumValues);
        }
        // dev-note : 2nd step : if no match was found by matching both entity name and model name, then try to match only by entity name (for simpler cases where a check for entity name in the parents is enough)
        BRxDataModelExcelRow targetFromParentSchemaNames = extractTargetUsingParentSchemaNames(parentSchemaNames, targets, propertyName);
        if (targetFromParentSchemaNames != null) {
            return buildSimpleNonBRxEnumValuesString(targetFromParentSchemaNames, apiEnumValues);
        }
        // dev-note : if no rows that match with the property name were found, we attempt to treat the flattened case
        if (!targets.isEmpty() && isPossiblyFlattenedParentSchemaNames(parentSchemaNames)) {
            return buildFlattenedNonBRxEnumValuesString(propertyName, apiEnumValues);
        }
        return null;
    }

    /** Method that builds the non brx enum values string in the simple case where there's a direct match between the open api property and brx property
     *
     * @param target the target brx data row
     * @param apiEnumValues the enum values that come from the open api spec
     * @return a string containing all the enum values that were not found in brx for that target row
     */
    private String buildSimpleNonBRxEnumValuesString(final BRxDataModelExcelRow target, final List<String> apiEnumValues) {
        String enumeratedByString = target.enumeratedBy();
        if (enumeratedByString != null) {
            final List<String> brxEnumValues = getBRxEnumValuesForTypeOrValue(target.enumeratedBy());
            final List<String> nonBRxEnumValues = filterNonBRxEnumValues(brxEnumValues, apiEnumValues);
            return String.join(", ", nonBRxEnumValues);
        }
        return null;
    }

    /** Method that builds the non brx enum values string in case of properties from flattened entities (which in this case is an actual enum value)
     *
     * @param propertyName the name of the open api property
     * @param apiEnumValues the enum values that come from the open api spec
     * @return a string containing all the enum values that were not found in brx for the flattened case
     */
    private String buildFlattenedNonBRxEnumValuesString(final String propertyName, final List<String> apiEnumValues) {
        // dev-note : try to find enum values in brx that correspond to the open api property name
        final List<String> propertyNameEnumValues = getBRxEnumValuesForTypeOrValue(propertyName);
        // dev-note : if none found, have the actual property name as the non brx enum value
        if (propertyNameEnumValues.isEmpty()) {
            return propertyName;
        }
        final List<String> nonBRxEnumValues = filterNonBRxEnumValues(propertyNameEnumValues, apiEnumValues);
        return String.join(", ", nonBRxEnumValues);
    }

    /** Method to check if a schema name is part of the list of schemas that might be flattened in the openapi spec
     *
     * @param parentSchemaName the schema name to check if is possibly flattened
     * @return true or false
     */
    private boolean isPossiblyFlattenedEntity(String parentSchemaName) {
        return possiblyFlattenedEntityShortLabelNames
                .stream()
                .anyMatch(parentSchemaName::equalsIgnoreCase);
    }

    /** Method that gets from a list of parent schema names, the schema name that is part of the possibly flattened entities list
     *
     * @param parentSchemaNames list of schema name path from root to the property
     * @return the name of the parent schema name that is possibly flattened
     */
    private String getPossiblyFlattenedSchemaNameFromParentSchemaNames(final List<String> parentSchemaNames) {
        for (int i = parentSchemaNames.size() - 1; i >= 1; i--) {
            if (isPossiblyFlattenedEntity(parentSchemaNames.get(i))) {
                return parentSchemaNames.get(i);
            }
        }
        return null;
    }

    /** Method that checks if any of the parent schema name is part of the possibly flattened entities list
     *
     * @param parentSchemaNames list of schema name path from root to the property
     * @return true or false
     */
    private boolean isPossiblyFlattenedParentSchemaNames(final List<String> parentSchemaNames) {
        return getPossiblyFlattenedSchemaNameFromParentSchemaNames(parentSchemaNames) != null;
    }

    /** Method that searches for enum values based on both the enum type and the enum value
     *
     * @param enumTypeOrValue the enum type or value to search for brx enum values with
     * @return a list of brx enum values
     */
    private List<String> getBRxEnumValuesForTypeOrValue(final String enumTypeOrValue) {
        return excelParser.getBRxEnumerationMetadata().records().stream()
                .filter(row -> row.enumerationType().equalsIgnoreCase(enumTypeOrValue) || row.enumerationValue().equalsIgnoreCase(enumTypeOrValue))
                .map(BRxEnumerationMetadataExcelRow::enumerationValue)
                // Part 1: Hack to handle NO enum values in the OpenAPI Spec
                .map(enumerationValue -> enumerationValue.equalsIgnoreCase("no") ? "NO" : enumerationValue)
                .toList();
    }

    /** Method that filters from the list of open api enum values only the ones that are not found in the brx values list
     *
     * @param brxEnumValues a list of brx enum values
     * @param apiEnumValues a list of enum values coming from the open api spec
     * @return a list of enum values that are found in the open api spec list, but not in brx list
     */
    private List<String> filterNonBRxEnumValues(List<String> brxEnumValues, List<String> apiEnumValues) {
        return apiEnumValues.stream()
                .filter(Objects::nonNull)
                // Part 2: Hack to handle NO enum values in the OpenAPI Spec
                .map(openApiEnumValue -> openApiEnumValue.equalsIgnoreCase("false") ? "NO" : openApiEnumValue)
                .filter(openApiEnumValue -> !brxEnumValues.contains(openApiEnumValue))
                .toList();
    }

    /** Method that checks if property from the open api spec matches with the shortened label name of the brx data row
     *
     * @param propertyName the property name coming from the open api spec
     * @param brxDataModelExcelRow the brx data row to do the check against
     * @return true or false
     */
    private boolean isPropertyNameAShortenedLabelName(String propertyName, BRxDataModelExcelRow brxDataModelExcelRow) {
        return propertyName.equalsIgnoreCase(brxDataModelExcelRow.shortenedLabelName());
    }
}
