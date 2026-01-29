package com.broadridge.brx.matmapparser.parsers;

import com.broadridge.brx.matmapparser.parsers.loaders.LogicalModelLoadingStrategy;
import com.broadridge.brx.matmapparser.parsers.loaders.LogicalModelLoadingStrategyType;
import com.broadridge.brx.matmapparser.logicalmodel.enumeration.BRxEnumerationMetadata;
import com.broadridge.brx.matmapparser.logicalmodel.enumeration.BRxEnumerationMetadataExcelColumn;
import com.broadridge.brx.matmapparser.logicalmodel.enumeration.BRxEnumerationMetadataExcelRow;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModelExcelColumn;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModel;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModelExcelRow;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class BRxLogicalModelExcelParser implements ExcelParser {

    private final String[] ignoredSheets;
    private final Map<String, LogicalModelLoadingStrategy> logicalModelLoadingStrategies;
    private final LogicalModelLoadingStrategyType loadingStrategyType;

    private final List<BRxDataModel> brxDataModels = new ArrayList<>();
    private BRxEnumerationMetadata brxEnumerationMetadata = new BRxEnumerationMetadata(Collections.emptyList());

    public BRxLogicalModelExcelParser(String[] ignoredSheets,
                                      Map<String, LogicalModelLoadingStrategy> logicalModelLoadingStrategies,
                                      LogicalModelLoadingStrategyType loadingStrategyType) {
        this.ignoredSheets = ignoredSheets;
        this.logicalModelLoadingStrategies = logicalModelLoadingStrategies;
        this.loadingStrategyType = loadingStrategyType;
        parseExcelFile();
    }

    @Override
    public List<BRxDataModel> getBrxDataModels() {
        if (brxDataModels.isEmpty()) {
            log.warn("No data models found. Please check the file or loading strategy");
        }
        return brxDataModels;
    }

    @Override
    public BRxEnumerationMetadata getBRxEnumerationMetadata() {
        if (brxEnumerationMetadata.records().isEmpty()) {
            log.warn("No enumeration metadata found. Please check the file or loading strategy");
        }
        return brxEnumerationMetadata;
    }

    @SneakyThrows
    private void parseExcelFile() {
        InputStream inputStream = logicalModelLoadingStrategies.get(loadingStrategyType.getName()).loadLogicalModel();

        try (final XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            workbook.sheetIterator().forEachRemaining(sheet -> {
                if (List.of(ignoredSheets).contains(sheet.getSheetName())) {
                    log.info("Skipping sheet '{}' because it is on the ignored list", sheet.getSheetName());
                } else {
                    log.info("Reading sheet: {}", sheet.getSheetName());

                    if (sheet.getSheetName().contains("Model")) {
                        extractBRxDataModels(sheet);
                    } else if (sheet.getSheetName().contains("Enumeration")) {
                        extractBRxEnumerationMetadata(sheet);
                    } else {
                        log.warn("Sheet '{}' does not match any known model or enumeration", sheet.getSheetName());
                    }
                }
            });
        }

        log.info("BRx logical model file parsed successfully");
    }

    private void extractBRxDataModels(Sheet sheet) {
        final List<BRxDataModelExcelRow> bRxDataModelExcelRows = extractDataModelRows(sheet);
        brxDataModels.add(new BRxDataModel(bRxDataModelExcelRows.getFirst().dataModel(), bRxDataModelExcelRows));
        log.info("Extracted {} data model rows for model: {}", bRxDataModelExcelRows.size(), bRxDataModelExcelRows.getFirst().dataModel());
    }

    private List<BRxDataModelExcelRow> extractDataModelRows(Sheet sheet) {
        List<BRxDataModelExcelRow> rows = new ArrayList<>();

        sheet.rowIterator().forEachRemaining(row -> {
            if (row.getRowNum() == 0) {
                return; // skip header row
            }

            BRxDataModelExcelRow dataModelRow = getBRxDataModelExcelRow(row);
            log.debug("Extracted data row: {}", dataModelRow);
            rows.add(dataModelRow);
        });

        return rows;
    }

    private BRxDataModelExcelRow getBRxDataModelExcelRow(Row row) {
        String seq = getCellValue(row.getCell(BRxDataModelExcelColumn.SEQ.getIndex()));
        boolean dependent = Boolean.parseBoolean(getCellValue(row.getCell(BRxDataModelExcelColumn.DEPENDENT.getIndex())));

        return new BRxDataModelExcelRow(
                getCellValue(row.getCell(BRxDataModelExcelColumn.DATA_MODEL.getIndex())),
                getCellValue(row.getCell(BRxDataModelExcelColumn.DATA_ENTITY.getIndex())),
                getCellValue(row.getCell(BRxDataModelExcelColumn.DATA_ATTRIBUTE.getIndex())),
                getCellValue(row.getCell(BRxDataModelExcelColumn.SHORTENED_ENTITY_NAME.getIndex())),
                getCellValue(row.getCell(BRxDataModelExcelColumn.SHORTENED_LABEL_NAME.getIndex())),
                !seq.isEmpty() ? (int) Double.parseDouble(seq) : 0,
                getCellValue(row.getCell(BRxDataModelExcelColumn.DATABASE_TYPE.getIndex())),
                getCellValue(row.getCell(BRxDataModelExcelColumn.XSD_DATATYPE.getIndex())),
                dependent,
                getCellValue(row.getCell(BRxDataModelExcelColumn.ENUMERATED_BY.getIndex())),
                getCellValue(row.getCell(BRxDataModelExcelColumn.DESCRIPTION.getIndex())),
                getCellValue(row.getCell(BRxDataModelExcelColumn.IS_REQUIRED.getIndex())),
                Boolean.parseBoolean(getCellValue(row.getCell(BRxDataModelExcelColumn.PII.getIndex()))),
                Boolean.parseBoolean(getCellValue(row.getCell(BRxDataModelExcelColumn.IS_EXTENSION.getIndex()))),
                Boolean.parseBoolean(getCellValue(row.getCell(BRxDataModelExcelColumn.DEPRECATED.getIndex())))
        );
    }

    private void extractBRxEnumerationMetadata(Sheet sheet) {
        List<BRxEnumerationMetadataExcelRow> bRxEnumerationMetadataExcelRows = extractEnumerationRows(sheet);

        log.info("Extracted {} Enumeration Metadata rows", bRxEnumerationMetadataExcelRows.size());
        brxEnumerationMetadata = new BRxEnumerationMetadata(bRxEnumerationMetadataExcelRows);
    }

    private List<BRxEnumerationMetadataExcelRow> extractEnumerationRows(Sheet sheet) {
        List<BRxEnumerationMetadataExcelRow> bRxEnumerationMetadataExcelRows = new ArrayList<>();

        sheet.rowIterator().forEachRemaining(row -> {
            if (row.getRowNum() == 0) {
                return; // skip header row
            }

            final BRxEnumerationMetadataExcelRow enumerationMetadataRow = new BRxEnumerationMetadataExcelRow(
                    getCellValue(row.getCell(BRxEnumerationMetadataExcelColumn.ENUMERATION_TYPE.getIndex())),
                    getCellValue(row.getCell(BRxEnumerationMetadataExcelColumn.ENUMERATION_VALUE.getIndex())),
                    getCellValue(row.getCell(BRxEnumerationMetadataExcelColumn.ENUMERATION_DESCRIPTION.getIndex()))
            );
            log.debug("Extracted enumeration row: {}", enumerationMetadataRow);
            bRxEnumerationMetadataExcelRows.add(enumerationMetadataRow);
        });

        return bRxEnumerationMetadataExcelRows;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> getStringCellValue(cell);
        };
    }

    private String getStringCellValue(Cell cell) {
        return switch (cell.getStringCellValue()) {
            case "Y", "TRUE" -> "true";
            case "N", "FALSE" -> "false";
            default -> cell.getStringCellValue().trim();
        };
    }
}
