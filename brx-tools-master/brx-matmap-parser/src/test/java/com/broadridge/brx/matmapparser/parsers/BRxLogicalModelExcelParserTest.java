package com.broadridge.brx.matmapparser.parsers;

import com.broadridge.brx.matmapparser.parsers.loaders.LogicalModelLoadingStrategy;
import com.broadridge.brx.matmapparser.parsers.loaders.LogicalModelLoadingStrategyType;
import com.broadridge.brx.matmapparser.logicalmodel.enumeration.BRxEnumerationMetadata;
import com.broadridge.brx.matmapparser.logicalmodel.enumeration.BRxEnumerationMetadataExcelColumn;
import com.broadridge.brx.matmapparser.logicalmodel.enumeration.BRxEnumerationMetadataExcelRow;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModel;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModelExcelColumn;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModelExcelRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BRxLogicalModelExcelParserTest {

    @Mock
    private LogicalModelLoadingStrategy loadingStrategy;

    @Mock
    private LogicalModelLoadingStrategyType loadingStrategyType;

    private Map<String, LogicalModelLoadingStrategy> strategies;
    private String[] ignoredSheets;

    @BeforeEach
    void setUp() {
        strategies = new HashMap<>();
        strategies.put("testStrategy", loadingStrategy);
        ignoredSheets = new String[] {"IgnoredSheet1", "IgnoredSheet2", "EnumerationSource Metadata"};
        when(loadingStrategyType.getName()).thenReturn("testStrategy");
    }

    @Test
    void getBrxDataModels_shouldReturnTestModels() throws Exception {
        InputStream mockInputStream = createMockExcelWithDataModel();
        when(loadingStrategy.loadLogicalModel()).thenReturn(mockInputStream);


        BRxLogicalModelExcelParser parser = new BRxLogicalModelExcelParser(ignoredSheets, strategies, loadingStrategyType);
        List<BRxDataModel> dataModels = parser.getBrxDataModels();

        assertNotNull(dataModels);
        assertEquals(1, dataModels.size());

        BRxDataModel model = dataModels.getFirst();
        assertEquals("TestModel", model.name());

        List<BRxDataModelExcelRow> rows = model.records();
        assertEquals(2, rows.size());

        BRxDataModelExcelRow firstRow = rows.getFirst();
        assertEquals("TestModel", firstRow.dataModel());
        assertEquals("Entity1", firstRow.dataEntity());
        assertEquals("Attribute1", firstRow.dataAttribute());
        assertEquals(10, firstRow.seq());
        assertFalse(firstRow.deprecated());
    }

    @Test
    void getBRxEnumerationMetadata_shouldReturnTestEnumerations() throws Exception {
        InputStream mockInputStream = createMockExcelWithEnumerationMetadata();
        when(loadingStrategy.loadLogicalModel()).thenReturn(mockInputStream);

        BRxLogicalModelExcelParser parser = new BRxLogicalModelExcelParser(ignoredSheets, strategies, loadingStrategyType);
        BRxEnumerationMetadata metadata = parser.getBRxEnumerationMetadata();

        assertNotNull(metadata);
        assertNotNull(metadata.records());
        assertEquals(2, metadata.records().size());
        BRxEnumerationMetadataExcelRow firstRow = metadata.records().getFirst();
        assertEquals("Type1", firstRow.enumerationType());
        assertEquals("Value1", firstRow.enumerationValue());
        assertEquals("Description1", firstRow.enumerationDescription());
    }

    @Test
    void getBrxDataModels_shouldReturnEmptyDataModelList() throws Exception {
        InputStream mockInputStream = createMockExcelWithIgnoredSheet();
        when(loadingStrategy.loadLogicalModel()).thenReturn(mockInputStream);

        BRxLogicalModelExcelParser parser = new BRxLogicalModelExcelParser(ignoredSheets, strategies, loadingStrategyType);
        List<BRxDataModel> dataModels = parser.getBrxDataModels();

        assertTrue(dataModels.isEmpty());
    }

    @Test
    void getBrxDataModelsAndEnumerations_shouldReturnEmptyListOfData() throws Exception {
        InputStream mockInputStream = createEmptyMockExcel();
        when(loadingStrategy.loadLogicalModel()).thenReturn(mockInputStream);

        BRxLogicalModelExcelParser parser = new BRxLogicalModelExcelParser(ignoredSheets, strategies, loadingStrategyType);

        assertTrue(parser.getBrxDataModels().isEmpty());
        assertTrue(parser.getBRxEnumerationMetadata().records().isEmpty());
    }

    @Test
    void getBrxDataModels_stringBooleansConvertToRealBooleans() throws Exception {
        InputStream mockInputStream = createMockExcelWithBooleanValues();
        when(loadingStrategy.loadLogicalModel()).thenReturn(mockInputStream);

        BRxLogicalModelExcelParser parser = new BRxLogicalModelExcelParser(ignoredSheets, strategies, loadingStrategyType);
        List<BRxDataModel> dataModels = parser.getBrxDataModels();

        BRxDataModelExcelRow row = dataModels.getFirst().records().getFirst();
        System.out.println(row);
        assertTrue(row.dependent());
        assertTrue(row.pii());
        assertTrue(row.extension());
    }

    @Test
    void getBrxDataModels_handleNullCells() throws Exception {
        InputStream mockInputStream = createMockExcelWithNullCells();
        when(loadingStrategy.loadLogicalModel()).thenReturn(mockInputStream);

        // Act
        BRxLogicalModelExcelParser parser = new BRxLogicalModelExcelParser(ignoredSheets, strategies, loadingStrategyType);
        List<BRxDataModel> dataModels = parser.getBrxDataModels();

        // Assert
        BRxDataModelExcelRow row = dataModels.getFirst().records().getFirst();
        assertEquals("", row.description());
        assertEquals(0, row.seq());
        assertFalse(row.dependent());
    }

    @Test
    void getBrxDataModels_shouldSkipEnumerationSource() throws Exception {
        InputStream mockInputStream = createMockExcelWithEnumerationSourceMetadata();
        when(loadingStrategy.loadLogicalModel()).thenReturn(mockInputStream);

        BRxLogicalModelExcelParser parser = new BRxLogicalModelExcelParser(ignoredSheets, strategies, loadingStrategyType);

        assertTrue(parser.getBrxDataModels().isEmpty());
    }

    @Test
    void parseBrxLogicalModel_noMatchingSheet() throws Exception {
        InputStream mockInputStream = createMockExcelWithDummyModel();
        when(loadingStrategy.loadLogicalModel()).thenReturn(mockInputStream);

        BRxLogicalModelExcelParser parser = new BRxLogicalModelExcelParser(ignoredSheets, strategies, loadingStrategyType);

        assertTrue(parser.getBrxDataModels().isEmpty());
        assertTrue(parser.getBRxEnumerationMetadata().records().isEmpty());
    }

    private InputStream createMockExcelWithDummyModel() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.createSheet("Dummy Sheet");
        return workbookToInputStream(workbook);
    }

    private InputStream createMockExcelWithDataModel() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestModel");

        Row headerRow = sheet.createRow(0);
        createCellsForDataModelHeader(headerRow);

        Row dataRow1 = sheet.createRow(1);
        createCellsForDataModelRow(dataRow1, "TestModel", "Entity1", "Attribute1", 10, false);

        Row dataRow2 = sheet.createRow(2);
        createCellsForDataModelRow(dataRow2, "TestModel", "Entity2", "Attribute2", 20, false);

        return workbookToInputStream(workbook);
    }

    private InputStream createMockExcelWithEnumerationMetadata() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Enumeration Metadata");

        Row headerRow = sheet.createRow(0);
        createCellsForEnumerationHeader(headerRow);

        Row dataRow1 = sheet.createRow(1);
        createCellsForEnumerationRow(dataRow1, "Type1", "Value1", "Description1");

        Row dataRow2 = sheet.createRow(2);
        createCellsForEnumerationRow(dataRow2, "Type2", "Value2", "Description2");

        return workbookToInputStream(workbook);
    }

    private InputStream createMockExcelWithIgnoredSheet() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.createSheet("IgnoredSheet1");
        return workbookToInputStream(workbook);
    }

    private InputStream createEmptyMockExcel() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        return workbookToInputStream(workbook);
    }

    private InputStream createMockExcelWithBooleanValues() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestModel");

        Row headerRow = sheet.createRow(0);
        createCellsForDataModelHeader(headerRow);

        Row dataRow = sheet. createRow(1);
        createCellsForDataModelRowWithBooleans(dataRow);

        return workbookToInputStream(workbook);
    }

    private InputStream createMockExcelWithNullCells() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestModel");

        Row headerRow = sheet.createRow(0);
        createCellsForDataModelHeader(headerRow);

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(BRxDataModelExcelColumn.DATA_MODEL.getIndex()).setCellValue("TestModel");
        dataRow.createCell(BRxDataModelExcelColumn.DATA_ENTITY.getIndex()).setCellValue("Entity1");
        dataRow.createCell(BRxDataModelExcelColumn.DATA_ATTRIBUTE.getIndex()).setCellValue("Attribute1");

        return workbookToInputStream(workbook);
    }

    private InputStream createMockExcelWithEnumerationSourceMetadata() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.createSheet("EnumerationSource Metadata");
        return workbookToInputStream(workbook);
    }

    private void createCellsForDataModelHeader(Row row) {
        row.createCell(BRxDataModelExcelColumn.DATA_MODEL.getIndex()).setCellValue("Data Model");
        row.createCell(BRxDataModelExcelColumn.DATA_ENTITY.getIndex()).setCellValue("Data Entity");
        row.createCell(BRxDataModelExcelColumn.DATA_ATTRIBUTE.getIndex()).setCellValue("Data Attribute");
    }

    private void createCellsForDataModelRow(Row row, String model, String entity,
                                            String attribute, int seq, boolean deprecated) {
        row.createCell(BRxDataModelExcelColumn.DATA_MODEL.getIndex()).setCellValue(model);
        row.createCell(BRxDataModelExcelColumn.DATA_ENTITY.getIndex()).setCellValue(entity);
        row.createCell(BRxDataModelExcelColumn.DATA_ATTRIBUTE.getIndex()).setCellValue(attribute);
        row.createCell(BRxDataModelExcelColumn.SHORTENED_ENTITY_NAME.getIndex()).setCellValue("Short" + entity);
        row.createCell(BRxDataModelExcelColumn.SHORTENED_LABEL_NAME.getIndex()).setCellValue("Label" + attribute);
        row.createCell(BRxDataModelExcelColumn.SEQ.getIndex()).setCellValue(seq);
        row.createCell(BRxDataModelExcelColumn.DATABASE_TYPE.getIndex()).setCellValue("VARCHAR");
        row.createCell(BRxDataModelExcelColumn.XSD_DATATYPE.getIndex()).setCellValue("string");
        row.createCell(BRxDataModelExcelColumn.DEPENDENT.getIndex()).setCellValue(false);
        row.createCell(BRxDataModelExcelColumn.ENUMERATED_BY.getIndex()).setCellValue("");
        row.createCell(BRxDataModelExcelColumn.DESCRIPTION.getIndex()).setCellValue("Test description");
        row.createCell(BRxDataModelExcelColumn.IS_REQUIRED.getIndex()).setCellValue("N");
        row.createCell(BRxDataModelExcelColumn.PII.getIndex()).setCellValue(false);
        row.createCell(BRxDataModelExcelColumn.IS_EXTENSION.getIndex()).setCellValue(false);
        row.createCell(BRxDataModelExcelColumn.DEPRECATED.getIndex()).setCellValue(deprecated);
    }

    private void createCellsForDataModelRowWithBooleans(Row row) {
        row.createCell(BRxDataModelExcelColumn.DATA_MODEL.getIndex()).setCellValue("TestModel");
        row.createCell(BRxDataModelExcelColumn.DATA_ENTITY.getIndex()).setCellValue("Entity1");
        row.createCell(BRxDataModelExcelColumn.DATA_ATTRIBUTE.getIndex()).setCellValue("Attribute1");
        row.createCell(BRxDataModelExcelColumn.SHORTENED_ENTITY_NAME.getIndex()).setCellValue("ShortEntity");
        row.createCell(BRxDataModelExcelColumn.SHORTENED_LABEL_NAME.getIndex()).setCellValue("LabelAttr");
        row.createCell(BRxDataModelExcelColumn.SEQ.getIndex()).setCellValue(10);
        row.createCell(BRxDataModelExcelColumn.DATABASE_TYPE.getIndex()).setCellValue("VARCHAR");
        row.createCell(BRxDataModelExcelColumn.XSD_DATATYPE.getIndex()).setCellValue("string");
        row.createCell(BRxDataModelExcelColumn.DEPENDENT.getIndex()).setCellValue("Y");
        row.createCell(BRxDataModelExcelColumn.ENUMERATED_BY.getIndex()).setCellValue("");
        row.createCell(BRxDataModelExcelColumn.DESCRIPTION.getIndex()).setCellValue("Test");
        row.createCell(BRxDataModelExcelColumn.IS_REQUIRED.getIndex()).setCellValue("Y");
        row.createCell(BRxDataModelExcelColumn.PII.getIndex()).setCellValue("TRUE");
        row.createCell(BRxDataModelExcelColumn.IS_EXTENSION.getIndex()).setCellValue(true);
        row.createCell(BRxDataModelExcelColumn.DEPRECATED.getIndex()).setCellValue(false);
    }

    private void createCellsForEnumerationHeader(Row row) {
        row.createCell(BRxEnumerationMetadataExcelColumn.ENUMERATION_TYPE.getIndex())
                .setCellValue("Enumeration Type");
        row.createCell(BRxEnumerationMetadataExcelColumn.ENUMERATION_VALUE.getIndex())
                .setCellValue("Enumeration Value");
        row.createCell(BRxEnumerationMetadataExcelColumn.ENUMERATION_DESCRIPTION.getIndex())
                .setCellValue("Enumeration Description");
    }

    private void createCellsForEnumerationRow(Row row, String type, String value, String description) {
        row.createCell(BRxEnumerationMetadataExcelColumn.ENUMERATION_TYPE.getIndex()).setCellValue(type);
        row.createCell(BRxEnumerationMetadataExcelColumn.ENUMERATION_VALUE.getIndex()).setCellValue(value);
        row.createCell(BRxEnumerationMetadataExcelColumn.ENUMERATION_DESCRIPTION.getIndex()).setCellValue(description);
    }

    private InputStream workbookToInputStream(XSSFWorkbook workbook) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }
}