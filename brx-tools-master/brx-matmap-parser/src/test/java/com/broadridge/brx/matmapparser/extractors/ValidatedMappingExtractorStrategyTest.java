package com.broadridge.brx.matmapparser.extractors;

import com.broadridge.brx.matmapparser.logicalmodel.extractor.BRxLogicalModelMappingExtractor;
import com.broadridge.brx.matmapparser.logicalmodel.validator.BRxLogicalModelMappingValidator;
import com.broadridge.brx.matmapparser.logicalmodel.validator.MappingValidator;
import com.broadridge.brx.matmapparser.model.Content;
import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.model.SpecFile;
import com.broadridge.brx.matmapparser.model.matmap.*;
import com.broadridge.brx.matmapparser.logicalmodel.extractor.MappingExtractor;
import com.broadridge.brx.matmapparser.parsers.ExcelParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.broadridge.brx.matmapparser.extractors.DummyMatmapData.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValidatedMappingExtractorStrategyTest {

    @Mock
    private ExcelParser excelParserMock;

    private ValidatedMappingExtractorStrategy validatedMappingExtractorStrategy;

    @BeforeEach
    void setUp() {
        MappingExtractor mappingExtractorMock = new BRxLogicalModelMappingExtractor(excelParserMock);
        MappingValidator mappingValidatorMock = new BRxLogicalModelMappingValidator(excelParserMock, mappingExtractorMock);
        validatedMappingExtractorStrategy = new ValidatedMappingExtractorStrategy(mappingValidatorMock);
    }

    @Test
    void extractMappings() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getNoBrxMatmapContent()))
                        .filename("no_brx.matmap")
                        .build()
        );

        List<Mapping> mappings = validatedMappingExtractorStrategy.extractMappings(specFiles);

        assertTrue(mappings.isEmpty());
    }

    private Translator getNoBrxMatmapContent() {
        MessageElement messageElement = getMessageElementWithParent("someSourceField", "someTargetField");
        MessageCategory messageCategory = getMessageCategory("categoryName");
        MessageDetail messageDetail = getMessageDetail("detailName");
        MessageDefinition messageDefinition = getMessageDefinition(messageElement, messageCategory, messageDetail);
        Message message = getMessage(messageDefinition);
        Messages messages = getMessages(List.of(message));
        return getTranslator(messages);
    }

}