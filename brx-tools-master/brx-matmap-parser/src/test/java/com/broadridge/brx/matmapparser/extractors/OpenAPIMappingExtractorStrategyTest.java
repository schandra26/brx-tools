package com.broadridge.brx.matmapparser.extractors;

import com.broadridge.brx.matmapparser.logicalmodel.enumeration.BRxEnumerationMetadata;
import com.broadridge.brx.matmapparser.logicalmodel.enumeration.BRxEnumerationMetadataExcelRow;
import com.broadridge.brx.matmapparser.logicalmodel.extractor.BRxLogicalModelMappingExtractor;
import com.broadridge.brx.matmapparser.logicalmodel.extractor.MappingExtractor;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModel;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModelExcelRow;
import com.broadridge.brx.matmapparser.logicalmodel.validator.BRxLogicalModelMappingValidator;
import com.broadridge.brx.matmapparser.logicalmodel.validator.MappingValidator;
import com.broadridge.brx.matmapparser.model.Content;
import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.model.SpecFile;
import com.broadridge.brx.matmapparser.parsers.ExcelParser;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenAPIMappingExtractorStrategyTest {

    private static final Date LAST_COMMITTED_AT = Date.from(Instant.ofEpochMilli(1111111111111L));

    @Mock
    private ExcelParser excelParserMock;

    private MappingValidator mappingValidatorMock;

    private OpenAPIMappingExtractorStrategy openAPIMappingExtractorStrategy;

    private static Stream<Arguments> extractMappings_ParameterizedScenariosIncompleteInputDataFlags() {
        return Stream.of(
                arguments(false, false),
                arguments(true, false)
        );
    }

    @BeforeEach
    void setUp() {
        MappingExtractor mappingExtractorMock = new BRxLogicalModelMappingExtractor(excelParserMock);
        mappingValidatorMock = new BRxLogicalModelMappingValidator(excelParserMock, mappingExtractorMock);
        openAPIMappingExtractorStrategy = new OpenAPIMappingExtractorStrategy(mappingExtractorMock, mappingValidatorMock);
    }

    @ParameterizedTest
    @MethodSource("extractMappings_ParameterizedScenariosIncompleteInputDataFlags")
    void extractMappings_ParameterizedIncompleteInputData(boolean shouldHaveObjects, boolean shouldHaveProperties) {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(getContent("position", shouldHaveObjects, shouldHaveProperties))
                        .build()
        );

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(specFiles);

        assertTrue(mappings.isEmpty());
    }

    @Test
    void extractMappings_unknownProperty() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(getContent("position", true, true))
                        .build()
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(specFiles);

        assertEquals(11, mappings.size());
    }

    @Test
    void extractMappings_stringPropertyIsBRx() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .filename("test1.yaml")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .content(getContent("position", true, true))
                        .build()
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(specFiles);

        assertFalse(mappings.isEmpty());
        assertEquals("test1.yaml", mappings.getFirst().getFilename());
        assertEquals("John Doe", mappings.getFirst().getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.getFirst().getLastCommittedAt());
        assertEquals("position.positionId", mappings.getFirst().getSourceField());
        assertEquals("Position.PositionEntity -> PositionId", mappings.getFirst().getTargetField());
    }

    @Test
    void extractMappings_stringPropertyNotBrx() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .filename("test1.yaml")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .content(getContent("position", true, true))
                        .build()
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(specFiles);

        assertFalse(mappings.isEmpty());
        assertEquals("test1.yaml", mappings.get(2).getFilename());
        assertEquals("John Doe", mappings.get(2).getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.get(2).getLastCommittedAt());
        assertEquals("position.positionTest", mappings.get(2).getSourceField());
        assertNull(mappings.get(2).getTargetField());
    }

    @Test
    void extractMappings_enumPropertyHasNonBrxEnumerationValues() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .filename("test1.yaml")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .content(getContent("position", true, true))
                        .build()
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(specFiles);

        assertFalse(mappings.isEmpty());
        assertEquals("test1.yaml", mappings.get(1).getFilename());
        assertEquals("John Doe", mappings.get(1).getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.get(1).getLastCommittedAt());
        assertFalse(mappings.get(1).getNonBrxEnumerationMetadata().isEmpty());
        assertEquals("four", mappings.get(1).getNonBrxEnumerationMetadata());
    }

    @Test
    void extractMappings_enumPropertyHasBrxEnumerationValues() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .filename("test1.yaml")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .content(getContent("position", true, true))
                        .build()
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(specFiles);

        assertFalse(mappings.isEmpty());
        assertEquals("test1.yaml", mappings.getFirst().getFilename());
        assertEquals("John Doe", mappings.getFirst().getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.getFirst().getLastCommittedAt());
        assertEquals("", mappings.getFirst().getNonBrxEnumerationMetadata());
    }

    @Test
    void extractMappings_numberProperty() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .filename("test1.yaml")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .content(getContent("position", true, true))
                        .build()
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(specFiles);

        assertFalse(mappings.isEmpty());
        assertEquals("test1.yaml", mappings.get(3).getFilename());
        assertEquals("John Doe", mappings.get(3).getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.get(3).getLastCommittedAt());
        assertEquals("position.positionTestNumber", mappings.get(3).getSourceField());
        assertEquals("Position.PositionEntity -> PositionTestNumber", mappings.get(3).getTargetField());
    }

    @Test
    void extractMappings_integerProperty() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .filename("test1.yaml")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .content(getContent("position", true, true))
                        .build()
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(specFiles);

        assertFalse(mappings.isEmpty());
        assertEquals("test1.yaml", mappings.get(4).getFilename());
        assertEquals("John Doe", mappings.get(4).getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.get(4).getLastCommittedAt());
        assertEquals("position.positionTestInteger", mappings.get(4).getSourceField());
        assertEquals("Position.PositionEntity -> PositionTestInteger", mappings.get(4).getTargetField());
    }

    @Test
    void extractMappings_booleanProperty() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .filename("test1.yaml")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .content(getContent("position", true, true))
                        .build()
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(specFiles);

        assertFalse(mappings.isEmpty());
        assertEquals("test1.yaml", mappings.get(5).getFilename());
        assertEquals("John Doe", mappings.get(5).getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.get(5).getLastCommittedAt());
        assertEquals("position.positionTestBoolean", mappings.get(5).getSourceField());
        assertEquals("Position.PositionEntity -> PositionTestBoolean", mappings.get(5).getTargetField());
    }

    @Test
    void extractMappings_objectProperty() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .filename("test1.yaml")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .content(getContent("position", true, true))
                        .build()
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(specFiles);

        assertFalse(mappings.isEmpty());
        assertEquals("test1.yaml", mappings.get(6).getFilename());
        assertEquals("John Doe", mappings.get(6).getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.get(6).getLastCommittedAt());
        assertEquals("position.positionTestObject.objectProperty", mappings.get(6).getSourceField());
        assertEquals("Position.PositionObjectEntity -> ObjectProperty", mappings.get(6).getTargetField());
    }

    @Test
    void extractMappings_arrayProperty() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .filename("test1.yaml")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .content(getContent("position", true, true))
                        .build()
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(specFiles);

        assertFalse(mappings.isEmpty());
        assertEquals(11, mappings.size());
    }

    @Test
    void extractMappings_schemaWithoutProperties() {
        Schema<Object> schema = new Schema<>();
        schema.setType("object"); // but no properties
        Components components = new Components().addSchemas("EmptyObject", schema);
        OpenAPI openAPI = new OpenAPI().components(components);
        SpecFile specFile = SpecFile.builder().content(new Content(openAPI)).build();

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(List.of(specFile));
        assertTrue(mappings.isEmpty(), "Schema with no properties should produce no mappings");
    }

    @Test
    void extractMappings_schemaFromLookupTable() {
        MappingExtractor localMappingExtractor = new BRxLogicalModelMappingExtractor(excelParserMock);

        BRxLogicalModelMappingValidator localMappingValidator = new BRxLogicalModelMappingValidator(excelParserMock, localMappingExtractor);
        MappingValidator validatorSpy = spy(localMappingValidator);

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        doReturn(false).when(validatorSpy).isBRxDataModel("position");
        doReturn(true).when(validatorSpy).isAPIModelFromLookupTable("position");

        OpenAPIMappingExtractorStrategy localOpenAPIMappingExtractorStrategy = new OpenAPIMappingExtractorStrategy(localMappingExtractor, validatorSpy);

        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .filename("test1.yaml")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .content(getContent("position", true, true))
                        .build()
        );

        List<Mapping> mappings = localOpenAPIMappingExtractorStrategy.extractMappings(specFiles);

        assertFalse(mappings.isEmpty(), "Lookup table schema should be processed");
        assertEquals("position.positionId", mappings.getFirst().getSourceField());
        assertEquals("Position.PositionEntity -> PositionId", mappings.getFirst().getTargetField());
    }

    @Test
    void extractMappings_specFileWithoutLastCommittedAt() {
        SpecFile specFile = SpecFile.builder()
                .filename("test1.yaml")
                .lastCommitter("Jane Doe")
                .lastCommittedAt(null)
                .content(getContent("position", true, true))
                .build();

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(List.of(specFile));

        assertFalse(mappings.isEmpty());
        assertNull(mappings.getFirst().getLastCommittedAt());
    }

    @Test
    void extractMappings_specFileWithoutLastCommiter() {
        SpecFile specFile = SpecFile.builder()
                .filename("test1.yaml")
                .lastCommitter(null)
                .lastCommittedAt(LAST_COMMITTED_AT)
                .content(getContent("position", true, true))
                .build();

        when(excelParserMock.getBrxDataModels()).thenReturn(getMockBRxDataModels());
        when(excelParserMock.getBRxEnumerationMetadata()).thenReturn(getMockBRxEnumerationMetadata());

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(List.of(specFile));

        assertFalse(mappings.isEmpty());
        assertNull(mappings.getFirst().getLastCommitter());
    }

    @Test
    @SuppressWarnings("rawtypes")
    void extractMappings_schemaNameDoesNotMatchPattern() {
        Schema schema = new Schema<>();
        schema.setType("object");
        schema.setProperties(Map.of("field", new Schema<>().type("string")));

        Components components = new Components().addSchemas("UnrelatedNameXYZ", schema);
        OpenAPI openAPI = new OpenAPI().components(components);
        SpecFile specFile = SpecFile.builder().content(new Content(openAPI)).build();

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(List.of(specFile));
        assertTrue(mappings.isEmpty());
    }

    @Test
    @SuppressWarnings("rawtypes")
    void extractMappings_unknownPropertyType() {
        MappingExtractor mappingExtractorReal = new BRxLogicalModelMappingExtractor(excelParserMock);
        openAPIMappingExtractorStrategy = new OpenAPIMappingExtractorStrategy(mappingExtractorReal, new BRxLogicalModelMappingValidator(excelParserMock, mappingExtractorReal));

        Schema unknownTypeProperty = new Schema();
        unknownTypeProperty.setType("abc"); // triggers default branch

        Schema schema = new Schema();
        schema.setType("object");
        schema.addProperty("unknownProperty", unknownTypeProperty);

        Components components = new Components();
        components.setSchemas(Map.of("positionUnknown", schema));
        OpenAPI openAPI = new OpenAPI();
        openAPI.setComponents(components);
        Content content = new Content(openAPI);

        SpecFile specFile = SpecFile.builder()
                .filename("unknownType.yaml")
                .lastCommitter("Tester")
                .lastCommittedAt(LAST_COMMITTED_AT)
                .content(content)
                .build();

        List<Mapping> mappings = openAPIMappingExtractorStrategy.extractMappings(List.of(specFile));

        assertTrue(mappings.isEmpty(), "Unknown property type should not produce mappings but should be handled");
    }


    private Content getContent(String testSchemaName, boolean shouldHaveObjects, boolean shouldHaveProperties) {
        return new Content(getOpenAPI(testSchemaName, shouldHaveObjects, shouldHaveProperties));
    }

    private OpenAPI getOpenAPI(String testSchemaName, boolean shouldHaveObjects, boolean shouldHaveProperties) {
        OpenAPI openAPI = new OpenAPI();
        openAPI.setComponents(getComponents(testSchemaName, shouldHaveObjects, shouldHaveProperties));
        return openAPI;
    }

    @SuppressWarnings("rawtypes")
    private Components getComponents(String testSchemaName, boolean shouldHaveObjects, boolean shouldHaveProperties) {
        Map<String, Schema> schemas = new HashMap<>();
        schemas.put(testSchemaName, getSchema(shouldHaveObjects, shouldHaveProperties));

        Components components = new Components();
        components.setSchemas(schemas);
        return components;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Schema getSchema(boolean shouldHaveObjects, boolean shouldHaveProperties) {
        Schema schema = new Schema();
        if (shouldHaveObjects) {
            schema.setType("object");
        }
        if (shouldHaveProperties) {
            Schema unknownProperty = new Schema();
            unknownProperty.setType("abc");
            schema.addProperty("unknownProperty", unknownProperty);

            Schema positionIdProperty = new Schema();
            positionIdProperty.setType("string");
            positionIdProperty.setEnum(List.of("one", "two", "three"));
            schema.addProperty("positionId", positionIdProperty);

            Schema positionIdTypeProperty = new Schema();
            positionIdTypeProperty.setType("string");
            positionIdTypeProperty.setEnum(List.of("one", "two", "three", "four"));
            schema.addProperty("positionIdType", positionIdTypeProperty);

            Schema positionTestProperty = new Schema();
            positionTestProperty.setType("string");
            schema.addProperty("positionTest", positionTestProperty);

            Schema positionTestNumberProperty = new Schema();
            positionTestNumberProperty.setType("number");
            schema.addProperty("positionTestNumber", positionTestNumberProperty);

            Schema positionTestIntegerProperty = new Schema();
            positionTestIntegerProperty.setType("integer");
            schema.addProperty("positionTestInteger", positionTestIntegerProperty);

            Schema positionTestBooleanProperty = new Schema();
            positionTestBooleanProperty.setType("boolean");
            schema.addProperty("positionTestBoolean", positionTestBooleanProperty);

            Schema positionTestObjectProperty = new Schema();
            positionTestObjectProperty.setType("object");
            Schema objectProperty = new Schema();
            objectProperty.setType("string");
            positionTestObjectProperty.addProperty("objectProperty", objectProperty);
            schema.addProperty("positionTestObject", positionTestObjectProperty);

            Schema positionTestArrayEmptyProperty = new Schema();
            positionTestArrayEmptyProperty.setType("array");
            positionTestArrayEmptyProperty.setItems(null);
            schema.addProperty("positionTestArrayEmpty", positionTestArrayEmptyProperty);

            Schema positionTestArrayPropertyEmptyProperties = new Schema();
            positionTestArrayPropertyEmptyProperties.setType("array");
            Schema arrayType = new Schema().type("string");
            arrayType.setProperties(null);
            positionTestArrayPropertyEmptyProperties.setItems(arrayType);
            schema.addProperty("positionTestArrayEmptyProperties", positionTestArrayPropertyEmptyProperties);

            Schema positionTestArrayProperty = new Schema();
            positionTestArrayProperty.setType("array");
            Schema arrayStringProperty = new Schema().type("string");
            arrayStringProperty.setProperties(
                    Map.of(
                    "stringProperty", new Schema().type("string"),
                    "numberProperty", new Schema().type("number"),
                    "integerProperty", new Schema().type("integer"),
                    "booleanProperty", new Schema().type("boolean"),
                    "arrayProperty", new Schema().type("array"),
                    "unknownProperty", new Schema().type("abc")
                    )
            );
            positionTestArrayProperty.setItems(arrayStringProperty);
            schema.addProperty("positionTestArray", positionTestArrayProperty);
        }
        return schema;
    }

    private List<BRxDataModel> getMockBRxDataModels() {
        return List.of(new BRxDataModel("Position", List.of(
                new BRxDataModelExcelRow(
                        "Position", "PositionEntity", "PositionId",
                        "position", "positionId",
                        1, "dbType", "xsd", false,
                        "EnumType1", "desc", "required", false, false, false
                ),
                new BRxDataModelExcelRow(
                        "Position", "PositionEntity", "PositionIdType",
                        "position", "positionIdType",
                        1, "dbType", "xsd", false,
                        "EnumType1", "desc", "required", false, false, false
                ),
                new BRxDataModelExcelRow(
                        "Position", "PositionEntity", "PositionTestInteger",
                        "position", "positionTestInteger",
                        1, "dbType", "xsd", false,
                        "EnumType1", "desc", "required", false, false, false
                ),
                new BRxDataModelExcelRow(
                        "Position", "PositionEntity", "PositionTestNumber",
                        "position", "positionTestNumber",
                        1, "dbType", "xsd", false,
                        "EnumType1", "desc", "required", false, false, false
                ),
                new BRxDataModelExcelRow(
                        "Position", "PositionEntity", "PositionTestBoolean",
                        "position", "positionTestBoolean",
                        1, "dbType", "xsd", false,
                        "EnumType1", "desc", "required", false, false, false
                ),
                new BRxDataModelExcelRow(
                        "Position", "PositionObjectEntity", "ObjectProperty",
                        "positionTestObject", "objectProperty",
                        1, "dbType", "xsd", false,
                        "EnumType1", "desc", "required", false, false, false
                ),
                new BRxDataModelExcelRow(
                        "Position", "PositionArrayEntity", "StringProperty",
                        "positionTestArray", "stringProperty",
                        1, "dbType", "xsd", false,
                        "EnumType1", "desc", "required", false, false, false
                ),
                new BRxDataModelExcelRow(
                        "Position", "PositionArrayEntity", "IntegerProperty",
                        "positionTestArray", "integerProperty",
                        1, "dbType", "xsd", false,
                        "EnumType1", "desc", "required", false, false, false
                ),
                new BRxDataModelExcelRow(
                        "Position", "PositionArrayEntity", "NumberProperty",
                        "positionTestArray", "numberProperty",
                        1, "dbType", "xsd", false,
                        "EnumType1", "desc", "required", false, false, false
                ),
                new BRxDataModelExcelRow(
                        "Position", "PositionArrayEntity", "BooleanProperty",
                        "positionTestArray", "booleanProperty",
                        1, "dbType", "xsd", false,
                        "EnumType1", "desc", "required", false, false, false
                )
        )));
    }

    private BRxEnumerationMetadata getMockBRxEnumerationMetadata() {
        return new BRxEnumerationMetadata(List.of(
                new BRxEnumerationMetadataExcelRow("EnumType1", "one", ""),
                new BRxEnumerationMetadataExcelRow("EnumType1", "two", ""),
                new BRxEnumerationMetadataExcelRow("EnumType1", "three", "")
        ));
    }
}