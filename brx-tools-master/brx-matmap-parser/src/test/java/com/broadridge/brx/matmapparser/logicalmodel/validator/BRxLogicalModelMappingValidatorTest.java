package com.broadridge.brx.matmapparser.logicalmodel.validator;

import com.broadridge.brx.matmapparser.logicalmodel.extractor.MappingExtractor;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModel;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModelExcelRow;
import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.parsers.ExcelParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BRxLogicalModelMappingValidatorTest {

    @Mock
    private ExcelParser excelParserMock;

    @InjectMocks
    private BRxLogicalModelMappingValidator bRxLogicalModelMappingValidator;

    @Mock
    private MappingExtractor mappingExtractor;

    private Mapping mapping;
    private BRxDataModel brxDataModel;
    private List<BRxDataModelExcelRow> records;

    private static Stream<Arguments> isValid_ParameterizedScenariosTrueAssertionArguments() {
        return Stream.of(
                arguments("field", "entity", "field", "field"),
                arguments("field", "entity", "fieldExtra", "field"),
                arguments("abcdefg", "entity", "abc", "def"),
                arguments("Field", "entity", "field", "FIELD")
        );
    }

    private static Stream<Arguments> isValid_ParameterizedScenariosFalseAssertionArguments() {
        return Stream.of(
                arguments("field", "entity", "differentElement", "differentAttribute"),
                arguments("field", "entity", null, "field"),
                arguments("field", "entity", "field", null),
                arguments("field", "entity", "", "")
        );
    }

    @BeforeEach
    void setUp() {
        mapping = Mapping.builder().build();
        brxDataModel = mock(BRxDataModel.class);
    }

    @Test
    void isValid_targetFieldIsNull() {
        mapping.setTargetField(null);

        assertFalse(bRxLogicalModelMappingValidator.isValid(mapping));
        verifyNoInteractions(excelParserMock);
    }

    @Test
    void isValid_targetFieldIsEmpty() {
        mapping.setTargetField("");

        assertFalse(bRxLogicalModelMappingValidator.isValid(mapping));
        verifyNoInteractions(excelParserMock);
    }

    @Test
    void isValid_entityExcelDataModelIsNull() {
        mapping.setTargetField("field");
        mapping.setEntity("entity");

        when(excelParserMock.getBrxDataModels()).thenReturn(Collections.emptyList());

        assertFalse(bRxLogicalModelMappingValidator.isValid(mapping));
    }

    @ParameterizedTest
    @MethodSource("isValid_ParameterizedScenariosTrueAssertionArguments")
    void isValid_ParameterizedScenariosTrueAssertion(final String targetField, final String entity, final String elementNameReturn,
                                                     final String dataAttributeReturn) {
        mapping.setTargetField(targetField);
        mapping.setEntity(entity);

        BRxDataModelExcelRow row = mock(BRxDataModelExcelRow.class);
        when(row.shortenedLabelName()).thenReturn(elementNameReturn);
        when(row.dataAttribute()).thenReturn(dataAttributeReturn);

        records = Collections.singletonList(row);
        when(brxDataModel.records()).thenReturn(records);
        when(brxDataModel.name()).thenReturn(entity);
        when(excelParserMock.getBrxDataModels()).thenReturn(Collections.singletonList(brxDataModel));

        assertTrue(bRxLogicalModelMappingValidator.isValid(mapping));
    }

    @ParameterizedTest
    @MethodSource("isValid_ParameterizedScenariosFalseAssertionArguments")
    void isValid_ParameterizedScenariosFalseAssertion(final String targetField, final String entity, final String elementNameReturn,
                                        final String dataAttributeReturn) {
        mapping.setTargetField(targetField);
        mapping.setEntity(entity);

        BRxDataModelExcelRow row = mock(BRxDataModelExcelRow.class);
        when(row.shortenedLabelName()).thenReturn(elementNameReturn);
        when(row.dataAttribute()).thenReturn(dataAttributeReturn);

        records = Collections.singletonList(row);
        when(brxDataModel.records()).thenReturn(records);
        when(brxDataModel.name()).thenReturn(entity);
        when(excelParserMock.getBrxDataModels()).thenReturn(Collections.singletonList(brxDataModel));

        assertFalse(bRxLogicalModelMappingValidator.isValid(mapping));
    }

    @Test
    void isValid_multipleModels() {
        mapping.setTargetField("field");
        mapping.setEntity("entity2");

        BRxDataModel wrongModel = mock(BRxDataModel.class);
        when(wrongModel.name()).thenReturn("entity1");

        BRxDataModel correctModel = mock(BRxDataModel.class);
        when(correctModel.name()).thenReturn("entity2");

        BRxDataModelExcelRow row = mock(BRxDataModelExcelRow.class);
        when(row.shortenedLabelName()).thenReturn("field");
        when(row.dataAttribute()).thenReturn("field");

        records = Collections.singletonList(row);
        when(correctModel.records()).thenReturn(records);

        when(excelParserMock.getBrxDataModels()).thenReturn(Arrays.asList(wrongModel, correctModel));

        assertTrue(bRxLogicalModelMappingValidator.isValid(mapping));
    }

    @Test
    void isBRxDataModel_returnsTrue_whenModelExists() {
        String modelName = "SomeModel";
        BRxDataModel mockModel = new BRxDataModel("testModel", List.of(getExcelDataModelRow()));
        when(mappingExtractor.getBRxDataModel(modelName)).thenReturn(mockModel);

        assertTrue(bRxLogicalModelMappingValidator.isBRxDataModel(modelName));
    }

    @Test
    void isBRxDataModel_returnsFalse_whenModelDoesNotExist() {
        String modelName = "SomeModel";
        when(mappingExtractor.getBRxDataModel(anyString())).thenReturn(null);

        assertFalse(bRxLogicalModelMappingValidator.isBRxDataModel(modelName));
    }

    @Test
    void isAPIModelFromLookupTable() {
        String modelName = "SomeModel";

        BRxDataModel model1 = new BRxDataModel("Model1", List.of(getExcelDataModelRow()));
        BRxDataModel model2 = new BRxDataModel("Model2", List.of(getExcelDataModelRow()));

        when(mappingExtractor.getBRxDataModelsFromLookupTable(anyString())).thenReturn(List.of(model1, model2));

        assertTrue(bRxLogicalModelMappingValidator.isAPIModelFromLookupTable(modelName));
    }

    @Test
    void isAPIModelFromLookupTable_noMatch() {
        String modelName = "SomeModel";
        when(mappingExtractor.getBRxDataModelsFromLookupTable(anyString())).thenReturn(Collections.emptyList());

        assertFalse(bRxLogicalModelMappingValidator.isAPIModelFromLookupTable(modelName));
    }

    @Test
    void isShortenedLabelName() {
        String dataModelName = "dataModel";
        String entityName = "balance";
        String propertyName = "balanceId";
        List<BRxDataModel> brxDataModels = List.of(
                new BRxDataModel(
                        dataModelName,
                        List.of(getExcelDataModelRow())
                )
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(brxDataModels);

        assertTrue(bRxLogicalModelMappingValidator.isBRxShortenedLabelName(dataModelName, entityName, propertyName));
    }

    @Test
    void isShortenedLabelName_noMatch() {
        String dataModelName = "dataModel";
        String entityName = "RandomEntityName";
        String propertyName = "RandomPropertyName";
        List<BRxDataModel> brxDataModels = List.of(
                new BRxDataModel(
                        dataModelName,
                        List.of(getExcelDataModelRow())
                )
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(brxDataModels);

        assertFalse(bRxLogicalModelMappingValidator.isBRxShortenedLabelName(dataModelName, entityName, propertyName));
    }

    @Test
    void isShortenedEntityName() {
        String dataModelName = "dataModel";
        String propertyName = "balance";
        List<BRxDataModel> brxDataModels = List.of(
                new BRxDataModel(
                        dataModelName,
                        List.of(getExcelDataModelRow())
                )
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(brxDataModels);

        assertTrue(bRxLogicalModelMappingValidator.isBRxShortenedEntityName(dataModelName, propertyName));
    }

    @Test
    void isShortenedEntityName_noMatch() {
        String dataModelName = "dataModel";
        String propertyName = "RandomShortenedEntityName";
        List<BRxDataModel> brxDataModels = List.of(
                new BRxDataModel(
                        dataModelName,
                        List.of(getExcelDataModelRow())
                )
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(brxDataModels);

        assertFalse(bRxLogicalModelMappingValidator.isBRxShortenedEntityName(dataModelName, propertyName));
    }

    @Test
    void containsBRxDataAttribute() {
        String propertyName = "BRx_dataAttribute";
        List<BRxDataModel> brxDataModels = List.of(
                new BRxDataModel(
                        "testModel",
                        List.of(getExcelDataModelRow())
                )
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(brxDataModels);

        assertTrue(bRxLogicalModelMappingValidator.containsBRxDataAttribute(propertyName));
    }

    @Test
    void containsBRxDataAttribute_noMatch() {
        String propertyName = "BRx_RandomDataAttribute";
        List<BRxDataModel> brxDataModels = List.of(
                new BRxDataModel(
                        "testModel",
                        List.of(getExcelDataModelRow())
                )
        );

        when(excelParserMock.getBrxDataModels()).thenReturn(brxDataModels);

        assertFalse(bRxLogicalModelMappingValidator.containsBRxDataAttribute(propertyName));
    }

    private BRxDataModelExcelRow getExcelDataModelRow() {
        return new BRxDataModelExcelRow(
                "dataModel",
                "Balance",
                "dataAttribute",
                "balance",
                "balanceId",
                1,
                "databaseType",
                "xsdDataType",
                false,
                "testEnumerationType",
                "description",
                "required",
                false,
                false,
                false
        );
    }
}