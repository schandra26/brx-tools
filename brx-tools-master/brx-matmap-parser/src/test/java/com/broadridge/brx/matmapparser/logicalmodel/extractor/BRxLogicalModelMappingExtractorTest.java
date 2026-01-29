package com.broadridge.brx.matmapparser.logicalmodel.extractor;

import com.broadridge.brx.matmapparser.logicalmodel.enumeration.BRxEnumerationMetadata;
import com.broadridge.brx.matmapparser.logicalmodel.enumeration.BRxEnumerationMetadataExcelRow;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModel;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModelExcelRow;
import com.broadridge.brx.matmapparser.parsers.ExcelParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BRxLogicalModelMappingExtractorTest {

    @Mock
    private ExcelParser excelParser;

    @InjectMocks
    private BRxLogicalModelMappingExtractor extractor;

    private BRxDataModelExcelRow rootRowNonDependent;
    private BRxDataModelExcelRow rootRowDependent;
    private BRxDataModelExcelRow flattenedRow;
    private BRxDataModel rootModel;
    private BRxDataModel nestedModel;
    private BRxDataModel flattenedModel;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Root-level row
        rootRowNonDependent = new BRxDataModelExcelRow(
                "Transaction", "TransactionRootEntity", "AttrRoot",
                "transactionRootEntity", "rootLabel",
                1, "dbType", "xsd", false,
                "EnumType1", "desc", "required", false, false, false
        );
        rootRowDependent = new BRxDataModelExcelRow(
                "Transaction", "TransactionRootEntityDependent", "AttrRoot",
                "transactionRootEntityDependent", "rootLabel",
                1, "dbType", "xsd", true,
                "EnumType1", "desc", "required", false, false, false
        );

        rootModel = new BRxDataModel("Transaction", List.of(rootRowNonDependent, rootRowDependent));

        // Nested row (dependent)
        BRxDataModelExcelRow nestedRow = new BRxDataModelExcelRow(
                "Transaction", "NestedEntity", "AttrNested",
                "NestedEntity", "nestedLabel",
                2, "dbType", "xsd", true,
                null, "desc", "optional", false, false, false
        );

        nestedModel = new BRxDataModel("Transaction", List.of(nestedRow));

        // Flattened entity
        flattenedRow = new BRxDataModelExcelRow(
                "Transaction", "FlagsEntity", null,
                "flags", "anyLabel",
                3, "dbType", "xsd", true,
                null, "desc", "optional", false, false, false
        );

        flattenedModel = new BRxDataModel("Transaction", List.of(flattenedRow));

        // Mock BRxEnumerationMetadata
        BRxEnumerationMetadata enumMetadataMock = mock(BRxEnumerationMetadata.class);
        when(enumMetadataMock.records()).thenReturn(
                List.of(
                        new BRxEnumerationMetadataExcelRow("EnumType1", "YES", "desc"),
                        new BRxEnumerationMetadataExcelRow("EnumType1", "NO", "desc")
                )
        );
        when(excelParser.getBRxEnumerationMetadata()).thenReturn(enumMetadataMock);
    }

    @Test
    void testGetBRxDataModel_found() {
        when(excelParser.getBrxDataModels()).thenReturn(List.of(rootModel));
        BRxDataModel result = extractor.getBRxDataModel("Transaction");
        assertNotNull(result);
        assertEquals("Transaction", result.name());
    }

    @Test
    void testGetBRxDataModel_notFound() {
        when(excelParser.getBrxDataModels()).thenReturn(List.of(rootModel));
        BRxDataModel result = extractor.getBRxDataModel("Unknown");
        assertNull(result);
    }

    @Test
    void testGetBRxDataModelsFromLookupTable_match() {
        when(excelParser.getBrxDataModels()).thenReturn(List.of(rootModel));
        List<BRxDataModel> result = extractor.getBRxDataModelsFromLookupTable("Trade");
        assertEquals(List.of("Transaction"), result.stream().map(BRxDataModel::name).toList());
    }

    @Test
    void testGetBRxDataModelsFromLookupTable_noMatch() {
        List<BRxDataModel> result = extractor.getBRxDataModelsFromLookupTable("SomethingElse");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetBRxDataModelsFromLookupTable_nullInput() {
        List<BRxDataModel> result = extractor.getBRxDataModelsFromLookupTable(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractBRxTargets_rootMatch() {
        when(excelParser.getBrxDataModels()).thenReturn(List.of(rootModel));

        List<BRxDataModelExcelRow> result = extractor.extractBRxTargets(
                List.of("TransactionRootEntity"), "rootLabel", List.of(rootModel));

        assertEquals(1, result.size());
        assertEquals("AttrRoot", result.getFirst().dataAttribute());
    }

    @Test
    void testExtractBRxTargets_nestedMatch() {
        when(excelParser.getBrxDataModels()).thenReturn(List.of(nestedModel));

        List<BRxDataModelExcelRow> result = extractor.extractBRxTargets(
                List.of("TransactionRootEntity", "NestedEntity"), "nestedLabel", List.of(nestedModel));

        assertEquals(1, result.size());
        assertEquals("AttrNested", result.getFirst().dataAttribute());
    }

    @Test
    void testExtractBRxTargets_flattenedEntity() {
        when(excelParser.getBrxDataModels()).thenReturn(List.of(flattenedModel));

        List<BRxDataModelExcelRow> result = extractor.extractBRxTargets(
                List.of("TransactionRootEntity", "flags"), "anyLabel", List.of(flattenedModel));

        assertEquals(1, result.size());
        assertEquals("flags", result.getFirst().shortenedEntityName());
    }

    @Test
    void testExtractBRxTargets_noMatch() {
        when(excelParser.getBrxDataModels()).thenReturn(List.of(rootModel));

        List<BRxDataModelExcelRow> result = extractor.extractBRxTargets(
                List.of("UnknownEntity"), "unknownLabel", List.of(rootModel));

        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractMappingTargetString_simpleCase() {
        String result = extractor.extractMappingTargetString(List.of(rootRowNonDependent), "rootLabel", List.of("TransactionRootEntity"));
        assertEquals("Transaction.TransactionRootEntity -> AttrRoot", result);
    }

    @Test
    void testExtractNonBRxEnumValuesString_simpleCase() {
        String result = extractor.extractNonBRxEnumValuesString(
                List.of(rootRowNonDependent), "rootLabel", List.of("TransactionRootEntity"), List.of("YES", "NO", "MAYBE"));
        assertEquals("MAYBE", result);
    }

    @Test
    void testExtractMappingTargetString_complexMultipleTargets() {
        List<BRxDataModelExcelRow> targets = List.of(rootRowNonDependent, nestedModel.records().getFirst());

        String result = extractor.extractMappingTargetString(targets, "rootLabel", List.of("TransactionRootEntity", "NestedEntity"));
        assertEquals("Transaction.TransactionRootEntity -> AttrRoot", result);
    }

    @Test
    void testExtractMappingTargetString_flattenedTarget() {
        String result = extractor.extractMappingTargetString(
                List.of(flattenedModel.records().getFirst()), "anyLabel", List.of("TransactionRootEntity", "flags")
        );
        assertEquals("Transaction.FlagsEntity", result);
    }

    @Test
    void testExtractNonBRxEnumValuesString_complexMultipleTargets() {
        List<BRxDataModelExcelRow> targets = List.of(rootRowNonDependent, nestedModel.records().getFirst());

        String result = extractor.extractNonBRxEnumValuesString(
                targets, "rootLabel", List.of("TransactionRootEntity", "NestedEntity"), List.of("YES", "NO", "MAYBE")
        );

        assertEquals("MAYBE", result);
    }

    @Test
    void testExtractNonBRxEnumValuesString_flattenedEntity() {
        String result = extractor.extractNonBRxEnumValuesString(
                List.of(flattenedModel.records().getFirst()), "flags", List.of("TransactionRootEntity", "flags"), List.of("YES", "NO")
        );

        assertEquals("flags", result);
    }

    @Test
    void testExtractBRxTargets_emptyInputs() {
        List<BRxDataModelExcelRow> result = extractor.extractBRxTargets(
                List.of(), "anyProperty", List.of()
        );
        assertTrue(result.isEmpty());

        result = extractor.extractBRxTargets(null, "anyProperty", null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractMappingTargetString_noTargets() {
        String result = extractor.extractMappingTargetString(
                List.of(), "rootLabel", List.of("TransactionRootEntity"));
        assertNull(result);
    }

    @Test
    void testExtractNonBRxEnumValuesString_noExtraValues() {
        String result = extractor.extractNonBRxEnumValuesString(
                List.of(rootRowNonDependent), "rootLabel", List.of("TransactionRootEntity"), List.of("YES", "NO"));
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractNonBRxEnumValuesString_withFalseValue() {
        String result = extractor.extractNonBRxEnumValuesString(
                List.of(rootRowNonDependent), "rootLabel", List.of("TransactionRootEntity"), List.of("false", "YES"));
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractBRxTargets_flattenedParentSchemaNamesDetection() {
        when(excelParser.getBrxDataModels()).thenReturn(List.of(flattenedModel));

        List<BRxDataModelExcelRow> result = extractor.extractBRxTargets(
                List.of("TransactionRootEntity", "flags"), "anyLabel", List.of(flattenedModel));

        assertEquals(1, result.size());
        assertEquals("FlagsEntity", result.getFirst().dataEntity());
    }

    @Test
    void testGetBRxDataModelsFromLookupTable_caseInsensitiveMatch() {
        when(excelParser.getBrxDataModels()).thenReturn(List.of(rootModel));
        List<BRxDataModel> result = extractor.getBRxDataModelsFromLookupTable("trade");
        assertEquals(1, result.size());
        assertEquals("Transaction", result.getFirst().name());
    }

    @Test
    void testExtractBRxTargets_multipleModelsTriggersFoundTargetsLoop() {
        BRxDataModelExcelRow anotherRow = new BRxDataModelExcelRow(
                "Party", "PartyEntity", "AttrParty",
                "partyEntity", "rootLabel",
                1, "dbType", "xsd", false,
                null, "desc", "optional", false, false, false
        );
        BRxDataModel anotherModel = new BRxDataModel("PartyModel", List.of(anotherRow));

        List<BRxDataModel> brxModels = List.of(rootModel, anotherModel);

        List<String> parentSchemaNames = List.of("Transaction", "Party");

        List<BRxDataModelExcelRow> result = extractor.extractBRxTargets(
                parentSchemaNames,
                "rootLabel",
                brxModels
        );

        assertTrue(result.contains(anotherRow));
        assertEquals(1, result.size());
    }

    @Test
    void testExtractMappingTargetString_hitsExtractTargetUsingParentSchemaNames() {
        BRxDataModelExcelRow secondRow = new BRxDataModelExcelRow(
                "Transaction", "SecondEntity", "AttrSecond",
                "SecondEntity", "rootLabel",
                2, "dbType", "xsd", false,
                null, "desc", "optional", false, false, false
        );

        List<BRxDataModelExcelRow> targets = List.of(rootRowDependent, secondRow);
        List<String> parentSchemaNames = List.of("RootEntity", "SecondEntity");

        String result = extractor.extractMappingTargetString(targets, "rootLabel", parentSchemaNames);

        assertEquals("Transaction.SecondEntity -> AttrSecond", result);
    }

    @Test
    void testExtractMappingTargetString_flattenedBranch() {
        String result = extractor.extractMappingTargetString(
                List.of(flattenedRow), "anyProperty", List.of("RootEntity", "flags")
        );

        assertEquals("Transaction.FlagsEntity", result);
    }

    @Test
    void testExtractMappingTargetString_triggersFindBRxRowsByEntityName() {
        BRxDataModelExcelRow flattenedExample = new BRxDataModelExcelRow(
                "Transaction", "FlagsEntity1", "AttrFlags1",
                "flags1", "someLabel1", 1, "dbType", "xsd", false,
                null, "desc", "optional", false, false, false
        );

        List<BRxDataModelExcelRow> targets = List.of(flattenedRow, flattenedExample);
        List<String> parentSchemaNames = List.of("RootEntity", "flags");

        String result = extractor.extractMappingTargetString(targets, "someFlattenedLabel", parentSchemaNames);

        assertEquals("Transaction.FlagsEntity", result);
    }

    @Test
    void testExtractBRxTargets_extractPossiblyFlattenedRows() {
        BRxDataModel flattenedModelForTest = new BRxDataModel("Transaction", List.of(flattenedRow));

        List<BRxDataModelExcelRow> result = extractor.extractBRxTargets(
                List.of("TransactionRootEntity", "flags"),"anyFlattenedLabel", List.of(flattenedModelForTest));

        assertEquals(1, result.size());
        assertEquals("flags", result.getFirst().shortenedEntityName());
    }

    @Test
    void testBuildFlattenedNonBRxEnumValuesString_nonEmptyFilteredValues() {
        BRxEnumerationMetadata enumMetadataMock = mock(BRxEnumerationMetadata.class);
        when(enumMetadataMock.records()).thenReturn(
                List.of(
                        new BRxEnumerationMetadataExcelRow("EnumType1", "YES", "desc"),
                        new BRxEnumerationMetadataExcelRow("EnumType1", "NO", "desc"),
                        new BRxEnumerationMetadataExcelRow("anyFlattenedLabel", "anyFlattenedLabelValue", "desc")
                )
        );
        when(excelParser.getBRxEnumerationMetadata()).thenReturn(enumMetadataMock);

        List<BRxDataModelExcelRow> targets = List.of(flattenedRow);
        List<String> parentSchemaNames = List.of("TransactionRootEntity", "flags");
        List<String> apiEnumValues = List.of("YES", "NO", "MAYBE");

        String result = extractor.extractNonBRxEnumValuesString(targets, "anyFlattenedLabel", parentSchemaNames, apiEnumValues);

        assertEquals("YES, NO, MAYBE", result);
    }

}
