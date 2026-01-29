package com.broadridge.brx.matmapparser.util;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MappingsExcelCreatorTest {

    @Test
    void getMappingsExcelResource_withEmptyList_hasOnlyHeaderRows() throws IOException {
        List<Mapping> emptyMappings = new ArrayList<>();

        ByteArrayResource result = MappingsExcelCreator.getMappingsExcelResource(emptyMappings);

        assertNotNull(result);
        assertTrue(result.contentLength() > 0);

        try (Workbook workbook = new XSSFWorkbook(result.getInputStream())) {
            Sheet sheet = workbook.getSheet("Mappings");
            Row headerRow = sheet.getRow(0);

            assertNotNull(sheet);
            assertEquals(1, sheet.getPhysicalNumberOfRows());
            assertEquals("Mapping File", headerRow.getCell(0).getStringCellValue());
            assertEquals("Source Field", headerRow.getCell(1).getStringCellValue());
            assertEquals("Target Field", headerRow.getCell(2).getStringCellValue());
            assertEquals("Non Brx Enumeration Metadata", headerRow.getCell(3).getStringCellValue());
            assertEquals("Last Committer", headerRow.getCell(4).getStringCellValue());
            assertEquals("Last Committed At", headerRow.getCell(5).getStringCellValue());
            assertEquals("Scanned At", headerRow.getCell(6).getStringCellValue());
        }
    }

    @Test
    void getMappingsExcelResource_withMappings_hasCorrectNumberOfRows() throws IOException {
        ByteArrayResource result = MappingsExcelCreator.getMappingsExcelResource(getTestMappings());

        try (Workbook workbook = new XSSFWorkbook(result.getInputStream())) {
            Sheet sheet = workbook.getSheet("Mappings");
            assertNotNull(sheet);
            assertEquals("Mappings", sheet.getSheetName());
            assertEquals(3, sheet.getPhysicalNumberOfRows());
        }
    }

    @Test
    void getMappingsExcelResource_withMappings_containsMappingData() throws IOException {
        ByteArrayResource result = MappingsExcelCreator.getMappingsExcelResource(getTestMappings());

        try (Workbook workbook = new XSSFWorkbook(result.getInputStream())) {
            Sheet sheet = workbook.getSheet("Mappings");

            Row dataRow1 = sheet.getRow(1);
            assertEquals("test-mapping1.yaml", dataRow1.getCell(0).getStringCellValue());
            assertEquals("source.field1", dataRow1.getCell(1).getStringCellValue());
            assertEquals("target.field1", dataRow1.getCell(2).getStringCellValue());
            assertEquals("metadata1", dataRow1.getCell(3).getStringCellValue());
            assertEquals("user1@example.com", dataRow1.getCell(4).getStringCellValue());

            Row dataRow2 = sheet.getRow(2);
            assertEquals("test-mapping2.yaml", dataRow2.getCell(0).getStringCellValue());
            assertEquals("source.field2", dataRow2.getCell(1).getStringCellValue());
            assertEquals("target.field2", dataRow2.getCell(2).getStringCellValue());
            assertEquals("metadata2", dataRow2.getCell(3).getStringCellValue());
            assertEquals("user2@example.com", dataRow2.getCell(4).getStringCellValue());
        }
    }

    private List<Mapping> getTestMappings() {
        List<Mapping> testMappings = new ArrayList<>();

        Mapping mapping1 = new Mapping();
        mapping1.setFilename("test-mapping1.yaml");
        mapping1.setSourceField("source.field1");
        mapping1.setTargetField("target.field1");
        mapping1.setNonBrxEnumerationMetadata("metadata1");
        mapping1.setLastCommitter("user1@example.com");
        mapping1.setLastCommittedAt(Instant.now());
        mapping1.setCreatedAt(Instant.now());

        Mapping mapping2 = new Mapping();
        mapping2.setFilename("test-mapping2.yaml");
        mapping2.setSourceField("source.field2");
        mapping2.setTargetField("target.field2");
        mapping2.setNonBrxEnumerationMetadata("metadata2");
        mapping2.setLastCommitter("user2@example.com");
        mapping2.setLastCommittedAt(Instant.now());
        mapping2.setCreatedAt(Instant.now());

        testMappings.add(mapping1);
        testMappings.add(mapping2);

        return testMappings;
    }
}
