package com.broadridge.brx.matmapparser.util;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@UtilityClass
public class MappingsExcelCreator {

    public static ByteArrayResource getMappingsExcelResource(List<Mapping> mappings) {
        Workbook workbook = getWorkbook(mappings);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            workbook.write(byteArrayOutputStream);
            workbook.close();
        } catch (IOException e) {
            throw new IllegalStateException("Could not write workbook to byte array output stream", e);
        }

        byte[] excelFile = byteArrayOutputStream.toByteArray();
        return new ByteArrayResource(excelFile);
    }

    private Workbook getWorkbook(List<Mapping> mappings) {
        XSSFWorkbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Mappings");
        createHeaderRow(workbook, sheet);
        fillMappingData(workbook, sheet, mappings);
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        return workbook;
    }

    private void createHeaderRow(Workbook workbook, Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = getHeaderCellStyle(workbook);

        createHeaderCell(headerRow, 0, "Mapping File", headerStyle);
        createHeaderCell(headerRow, 1, "Source Field", headerStyle);
        createHeaderCell(headerRow, 2, "Target Field", headerStyle);
        createHeaderCell(headerRow, 3, "Non Brx Enumeration Metadata", headerStyle);
        createHeaderCell(headerRow, 4, "Last Committer", headerStyle);
        createHeaderCell(headerRow, 5, "Last Committed At", headerStyle);
        createHeaderCell(headerRow, 6, "Scanned At", headerStyle);
    }

    private void createHeaderCell(Row headerRow, int column, String value, CellStyle style) {
        Cell cell = headerRow.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void fillMappingData(Workbook workbook, Sheet sheet, List<Mapping> mappings) {
        CellStyle sourceFieldStyle = getSourceFieldCellStyle(workbook);
        int currentRow = 1;

        for (Mapping mapping : mappings) {
            Row row = sheet.createRow(currentRow++);

            Cell mappingFileCell = row.createCell(0);
            mappingFileCell.setCellValue(mapping.getFilename());

            Cell sourceFieldCell = row.createCell(1);
            sourceFieldCell.setCellValue(mapping.getSourceField());
            sourceFieldCell.setCellStyle(sourceFieldStyle);

            Cell targetFieldCell = row.createCell(2);
            targetFieldCell.setCellValue(mapping.getTargetField());

            Cell enumMetadataCell = row.createCell(3);
            enumMetadataCell.setCellValue(mapping.getNonBrxEnumerationMetadata());

            Cell lastCommitterCell = row.createCell(4);
            lastCommitterCell.setCellValue(mapping.getLastCommitter());

            Cell lastCommittedAtCell = row.createCell(5);
            lastCommittedAtCell.setCellValue(mapping.getLastCommittedAt().toString());

            Cell scannedAtCell = row.createCell(6);
            scannedAtCell.setCellValue(mapping.getCreatedAt().toString());
        }
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        cellStyle.setFont(font);

        return cellStyle;
    }

    private CellStyle getSourceFieldCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);

        return cellStyle;
    }
}
