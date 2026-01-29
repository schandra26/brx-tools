package com.broadridge.brx.matmapparser.extractors;

import com.broadridge.brx.matmapparser.logicalmodel.extractor.MappingExtractor;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModel;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModelExcelRow;
import com.broadridge.brx.matmapparser.logicalmodel.validator.MappingValidator;
import com.broadridge.brx.matmapparser.model.SpecFile;
import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service("directOpenAPI")
@RequiredArgsConstructor
public class OpenAPIMappingExtractorStrategy implements MappingExtractorStrategy {

    private final MappingExtractor mappingExtractor;
    private final MappingValidator mappingValidator;

    @Override
    public List<Mapping> extractMappings(List<SpecFile> specFiles) {
        List<Mapping> mappings = new ArrayList<>();

        for (SpecFile specFile : specFiles) {
            extractMappingsFromSpecFile(specFile, mappings);
        }

        return mappings;
    }

    @SuppressWarnings({"rawtypes", "unchecked"}) // Schema is a raw type, and it's really hard to parametrize it so we're suppressing it
    private void extractMappingsFromSpecFile(SpecFile specFile, List<Mapping> mappings) {

        final Map<String, Schema> schemas = specFile.getContent().getOpenAPI().getComponents().getSchemas();

        final Map<String, Map.Entry<String, Schema>> schemasOfInterestWithModelNames = getSchemasOfInterestWithModelNames(schemas);

        for (final Map.Entry<String, Map.Entry<String, Schema>> schemaOfInterestWithModelName : schemasOfInterestWithModelNames.entrySet()) {
            final String schemaModelName = schemaOfInterestWithModelName.getKey();
            if (schemaModelName != null) {
                final List<BRxDataModel> brxModels = getBRxModelsFromAPIModel(schemaModelName);
                final Map.Entry<String, Schema> schemaOfInterestEntry = schemaOfInterestWithModelName.getValue();
                final String schemaName = schemaOfInterestEntry.getKey();
                final Schema schema = schemaOfInterestEntry.getValue();

                log.info("Processing schema: {}", schemaName);

                List<String> parentSchemaNames = new LinkedList<>();
                parentSchemaNames.add(schemaName);

                processSchema(parentSchemaNames, schema.getProperties(), mappings, specFile, brxModels);
            } else {
                log.info("No schema was found that was suitable for processing within file: {}", specFile.getFilename());
            }
        }
    }

    @SuppressWarnings({"rawtypes"})
    private void processSchema(List<String> parentSchemaNames, Map<String, Schema> properties, List<Mapping> mappings, SpecFile specFile, List<BRxDataModel> brxModels) {
        for (Map.Entry<String, Schema> property : properties.entrySet()) {
            if (property.getValue().getType() != null) {
                switch (property.getValue().getType()) {
                    case "array" -> extractArrayProperties(parentSchemaNames, mappings, specFile, property, brxModels);
                    case "string" -> extractStringProperties(parentSchemaNames, mappings, specFile, property, brxModels);
                    case "number" -> extractNumberProperties(parentSchemaNames, mappings, specFile, property, brxModels);
                    case "integer" -> extractIntegerProperties(parentSchemaNames, mappings, specFile, property, brxModels);
                    case "boolean" -> extractBooleanProperties(parentSchemaNames, mappings, specFile, property, brxModels);
                    case "object" -> extractObjectProperties(parentSchemaNames, mappings, specFile, property, brxModels);
                    default ->
                            log.warn("Property '{}' has unknown type: '{}'. Skipping", property.getKey(), property.getValue().getType());
                }
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void extractObjectProperties(List<String> parentSchemaNames, List<Mapping> mappings, SpecFile specFile, Map.Entry<String, Schema> property, List<BRxDataModel> brxModels) {
        String objectName = property.getKey();
        Schema nestedSchema = property.getValue();

        log.info("Processing object property: {} in schema: {}", objectName, parentSchemaNames.getLast());

        Map<String, Schema> properties = nestedSchema.getProperties();
        if (properties == null) {
            log.info("Object property {} in schema {} has no properties to process.", objectName, parentSchemaNames.getLast());
            return;
        }

        List<String> updatedParentNames = new LinkedList<>(parentSchemaNames);
        updatedParentNames.add(objectName);

        processSchema(updatedParentNames, properties, mappings, specFile, brxModels);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void extractArrayProperties(List<String> parentSchemaNames, List<Mapping> mappings, SpecFile specFile, Map.Entry<String, Schema> property, List<BRxDataModel> brxModels) {
        String arraySchemaName = property.getKey();
        Schema itemsSchema = property.getValue().getItems();

        if (itemsSchema != null) {
            log.info("Processing array property: {} in schema: {}", arraySchemaName, parentSchemaNames.getLast());

            Map<String, Schema> properties = property.getValue().getItems().getProperties();
            if (properties == null) {
                log.info("Array property {} in schema {} has no properties to process. It may be an enum", arraySchemaName, parentSchemaNames.getLast());
                return;
            }

            List<String> updatedParentNames = new LinkedList<>(parentSchemaNames);
            updatedParentNames.add(arraySchemaName);

            processSchema(updatedParentNames, properties, mappings, specFile, brxModels);
        }
    }

    @SuppressWarnings({"rawtypes"})
    private void extractStringProperties(List<String> parentSchemaNames, List<Mapping> mappings, SpecFile specFile, Map.Entry<String, Schema> property, List<BRxDataModel> brxModels) {
        String propertyName = property.getKey();

        if (property.getValue().getEnum() == null) {
            log.info("Processing string property: '{}' in schema: '{}'", propertyName, parentSchemaNames.getLast());
            addSimpleMapping(parentSchemaNames, mappings, specFile, propertyName, brxModels);
        } else {
            log.info("Processing enum property: '{}' in schema: '{}'", propertyName, parentSchemaNames.getLast());
            addEnumeratedMapping(parentSchemaNames, mappings, specFile, property, propertyName, brxModels);
        }
    }

    @SuppressWarnings({"rawtypes"})
    private void extractNumberProperties(List<String> parentSchemaNames, List<Mapping> mappings, SpecFile specFile, Map.Entry<String, Schema> property, List<BRxDataModel> brxModels) {
        String propertyName = property.getKey();
        log.info("Processing number property: '{}' in schema: '{}'", propertyName, parentSchemaNames.getLast());
        addSimpleMapping(parentSchemaNames, mappings, specFile, propertyName, brxModels);
    }

    @SuppressWarnings({"rawtypes"})
    private void extractIntegerProperties(List<String> parentSchemaNames, List<Mapping> mappings, SpecFile specFile, Map.Entry<String, Schema> property, List<BRxDataModel> brxModels) {
        String propertyName = property.getKey();
        log.info("Processing integer property: '{}' in schema: '{}'", propertyName, parentSchemaNames.getLast());
        addSimpleMapping(parentSchemaNames, mappings, specFile, propertyName, brxModels);
    }

    @SuppressWarnings({"rawtypes"})
    private void extractBooleanProperties(List<String> parentSchemaNames, List<Mapping> mappings, SpecFile specFile, Map.Entry<String, Schema> property, List<BRxDataModel> brxModels) {
        String propertyName = property.getKey();
        log.info("Processing boolean property: '{}' in schema: '{}'", propertyName, parentSchemaNames.getLast());
        addSimpleMapping(parentSchemaNames, mappings, specFile, propertyName, brxModels);
    }

    private void addSimpleMapping(List<String> parentSchemaNames, List<Mapping> mappings, SpecFile specFile, String propertyName, List<BRxDataModel> brxModels) {
        final String mappingSourceString = String.join(".", parentSchemaNames) + "." + propertyName;

        // dev-note : extract the brx property rows that will be used for the target of the brx mapping (for both root and nested properties in the open api specs)
        // the list can contain multiple rows in case of rows of flattened entities or in case of rows that are selected from multiple entities (rows that have have the shortened label name matching with property name ex : type, value, sequence etc)
        final List<BRxDataModelExcelRow> brxMappingTargets = mappingExtractor.extractBRxTargets(parentSchemaNames, propertyName, brxModels);

        final String brxMappingTargetString = mappingExtractor.extractMappingTargetString(brxMappingTargets, propertyName, parentSchemaNames);

        addMapping(mappings, specFile, mappingSourceString, brxMappingTargetString, null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addEnumeratedMapping(List<String> parentSchemaNames, List<Mapping> mappings, SpecFile specFile, Map.Entry<String, Schema> property, String propertyName, List<BRxDataModel> brxModels) {
        final String mappingSourceString = String.join(".", parentSchemaNames) + "." + propertyName;

        final List<BRxDataModelExcelRow> brxMappingTargets = mappingExtractor.extractBRxTargets(parentSchemaNames, propertyName, brxModels);

        final String brxMappingTargetString = mappingExtractor.extractMappingTargetString(brxMappingTargets, propertyName, parentSchemaNames);

        final List<String> openApiEnumValues = property.getValue().getEnum();
        final String nonBRxEnumValuesString = mappingExtractor.extractNonBRxEnumValuesString(brxMappingTargets, propertyName, parentSchemaNames, openApiEnumValues);

        addMapping(mappings, specFile, mappingSourceString, brxMappingTargetString, nonBRxEnumValuesString);
    }

    private List<BRxDataModel> getBRxModelsFromAPIModel(final String apiModelName) {
        final BRxDataModel bRxDataModel = mappingExtractor.getBRxDataModel(apiModelName);
        if (bRxDataModel != null) {
            return List.of(bRxDataModel);
        } else {
            return mappingExtractor.getBRxDataModelsFromLookupTable(apiModelName);
        }
    }

    @SuppressWarnings("rawtypes")
    private Map<String,Map.Entry<String, Schema>> getSchemasOfInterestWithModelNames(final Map<String, Schema> schemaMap) {
        // dev-note : extract schemas of interest based on some regex patterns that were identified to target the desired schemas
        // (the key of the maps that are returned represent the api model name extracted from the actual schema name using the regex pattern)
        final Map<String, Map.Entry<String, Schema>> exactMatchSchemas = getSchemasWithPattern(schemaMap, "(.*)");

        final Map<String, Map.Entry<String, Schema>> dashSchemaSuffixSchemas = getSchemasWithPattern(schemaMap, "^(.*)-schema$");

        final Map<String, Map.Entry<String, Schema>> createRequestSchemas = getSchemasWithPattern(schemaMap, "^Create(.*)Request$");

        final Map<String, Map.Entry<String, Schema>> v2Schemas = getSchemasWithPattern(schemaMap, "^(.*)V2$");

        // dev-note : aggregate all the schemas found in a single map and keep the first schema for each api model name
        final Map<String, Map.Entry<String, Schema>> allSchemas = Stream.of(exactMatchSchemas, dashSchemaSuffixSchemas, createRequestSchemas, v2Schemas)
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (first, second) -> first // keep first
                ));

        final Map<String, Map.Entry<String, Schema>> schemasFromBRx = new HashMap<>();
        final Map<String, Map.Entry<String, Schema>> schemasFromLookupTable = new HashMap<>();

        // dev-note : split the schemas that have a 1-1 match with the brx data model from the ones are from the lookup table
        for (Map.Entry<String, Map.Entry<String, Schema>> entry : allSchemas.entrySet()) {
            if (mappingValidator.isAPIModelFromLookupTable(entry.getKey())) {
                schemasFromLookupTable.put(entry.getKey(), entry.getValue());
            } else {
                schemasFromBRx.put(entry.getKey(), entry.getValue());
            }
        }

        return !schemasFromLookupTable.isEmpty() ? schemasFromLookupTable : schemasFromBRx;
    }

    @SuppressWarnings("rawtypes")
    private Map<String,Map.Entry<String, Schema>> getSchemasWithPattern(final Map<String, Schema> schemaMap, final String regexPattern) {
        final Map<String,Map.Entry<String, Schema>> schemasWithModelNames = new HashMap<>();
        for (Map.Entry<String, Schema> entry : schemaMap.entrySet()) {
            final String schemaName = entry.getKey();
            final Schema schema = entry.getValue();

            final Pattern pattern = Pattern.compile(regexPattern);
            final Matcher matcher = pattern.matcher(schemaName);
            if (matcher.find())
            {
                // dev-note : extract the api model name form the schema name
                final String modelFromSchemaName = matcher.group(1);
                if (schema.getProperties() != null && "object".equals(schema.getType()) && modelFromSchemaName != null && isSchemaOfBRxInterest(modelFromSchemaName)) {
                    // dev-note : add in the map the api model name that was extracted as the key and the actual schema entry (with it's real name) as a value
                    schemasWithModelNames.put(modelFromSchemaName, entry);
                }
            }
        }
        return schemasWithModelNames;
    }

    private boolean isSchemaOfBRxInterest(final String modelFromSchemaName) {
        return mappingValidator.isBRxDataModel(modelFromSchemaName) || mappingValidator.isAPIModelFromLookupTable(modelFromSchemaName);
    }

    private void addMapping(List<Mapping> mappings, SpecFile specFile, String sourceField, String targetField, String nonBrxEnumerationMetadata) {
        Mapping mapping = Mapping.builder()
                .filename(specFile.getFilename())
                .sourceField(sourceField)
                .targetField(targetField)
                .nonBrxEnumerationMetadata(nonBrxEnumerationMetadata)
                .lastCommitter(specFile.getLastCommitter())
                .lastCommittedAt(specFile.getLastCommittedAt() != null ? specFile.getLastCommittedAt().toInstant() : null)
                .createdAt(Instant.now())
                .build();
        mappings.add(mapping);
    }
}
