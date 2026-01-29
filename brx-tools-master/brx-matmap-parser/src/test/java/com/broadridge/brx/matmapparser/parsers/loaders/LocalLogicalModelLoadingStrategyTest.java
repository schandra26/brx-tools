package com.broadridge.brx.matmapparser.parsers.loaders;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalLogicalModelLoadingStrategyTest {

    @Mock
    ResourceLoader resourceLoader;

    @Mock
    Resource resource;

    String filePath = "test/path/logical-model.xlsx";

    @Test
    void loadLogicalModel() throws IOException {
        when(resourceLoader.getResource(argThat(arg -> arg.contains(filePath)))).thenReturn(resource);

        XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(outputStream.toByteArray()));

        LocalLogicalModelLoadingStrategy localLogicalModelLoadingStrategy = new LocalLogicalModelLoadingStrategy(resourceLoader, filePath);

        InputStream inputStream = localLogicalModelLoadingStrategy.loadLogicalModel();

        assertNotNull(inputStream);
    }


}
