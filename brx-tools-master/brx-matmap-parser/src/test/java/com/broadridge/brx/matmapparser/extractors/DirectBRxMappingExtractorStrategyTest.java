package com.broadridge.brx.matmapparser.extractors;

import com.broadridge.brx.matmapparser.logicalmodel.validator.BRxLogicalModelMappingValidator;
import com.broadridge.brx.matmapparser.model.Content;
import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.model.SpecFile;
import com.broadridge.brx.matmapparser.model.matmap.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static com.broadridge.brx.matmapparser.extractors.DummyMatmapData.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DirectBRxMappingExtractorStrategyTest {

    private static final Date LAST_COMMITTED_AT = Date.from(Instant.ofEpochMilli(1111111111111L));

    private DirectBRxMappingExtractorStrategy directBRxMappingExtractorStrategy;

    @Mock
    private BRxLogicalModelMappingValidator bRxLogicalModelMappingValidator;

    @BeforeEach
    void setUp() {
        directBRxMappingExtractorStrategy = new DirectBRxMappingExtractorStrategy(bRxLogicalModelMappingValidator);
    }

    @Test
    void extractMappings_noBrx() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getNoBrxMatmapContent()))
                        .filename("no_brx.matmap")
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertTrue(mappings.isEmpty());
    }

    @Test
    void extractMappings_simple() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getSimpleMatmapContent()))
                        .filename("test.matmap")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertEquals("test.matmap", mappings.getFirst().getFilename());
        assertEquals("John Doe", mappings.getFirst().getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.getFirst().getLastCommittedAt());
        assertEquals("sourceField", mappings.getFirst().getSourceField());
        assertEquals("BRx_someTargetField;  *", mappings.getFirst().getTargetField());
    }

    @Test
    void extractMappings_filterIDX() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getIDXMatmapContent()))
                        .filename("test_idx.matmap")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertTrue(mappings.isEmpty());
    }

    @Test
    void extractMappings_simpleNested() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getSimpleNestedMatmapContent()))
                        .filename("test_nested.matmap")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertEquals("test_nested.matmap", mappings.getFirst().getFilename());
        assertEquals("sourceField", mappings.getFirst().getSourceField());
        assertEquals("John Doe", mappings.getFirst().getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.getFirst().getLastCommittedAt());
        assertEquals("BRx_someTargetField;  *", mappings.getFirst().getTargetField());
        assertEquals("test_nested.matmap", mappings.getLast().getFilename());
        assertEquals("childSourceField", mappings.getLast().getSourceField());
        assertEquals("BRx_someChildTargetField;  *", mappings.getLast().getTargetField());
    }

    @Test
    void extractMappings_simple_emptyChildList() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getSimpleEmptyChildElementsMatmapContent()))
                        .filename("test_nested.matmap")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertEquals("test_nested.matmap", mappings.getFirst().getFilename());
        assertEquals("John Doe", mappings.getFirst().getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.getFirst().getLastCommittedAt());
        assertEquals("sourceField", mappings.getFirst().getSourceField());
        assertEquals("BRx_someTargetField;  *", mappings.getFirst().getTargetField());
    }

    @Test
    void extractMappings_standard() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getStandardMappingContent()))
                        .filename("standard.matmap")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertEquals("standard.matmap", mappings.getFirst().getFilename());
        assertEquals("John Doe", mappings.getFirst().getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.getFirst().getLastCommittedAt());
        assertEquals("sourceField", mappings.getFirst().getSourceField());
        assertEquals("BRx_InstrumentOrigin = BIMS; BRx_InstrumentEntity = DEMOUS;  *", mappings.getFirst().getTargetField());
    }

    @Test
    void extractMappings_standardNested() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getStandardNestedMappingContent()))
                        .filename("standard_nested.matmap")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertEquals("standard_nested.matmap", mappings.getFirst().getFilename());
        assertEquals("sourceField", mappings.getFirst().getSourceField());
        assertEquals("John Doe", mappings.getFirst().getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.getFirst().getLastCommittedAt());
        assertEquals("BRx_InstrumentOrigin = BIMS; BRx_InstrumentEntity = DEMOUS;  *", mappings.getFirst().getTargetField());
        assertEquals("standard_nested.matmap", mappings.getLast().getFilename());
        assertEquals("childSourceField", mappings.getLast().getSourceField());
        assertEquals("BRx_InstrumentOriginChild = BIMS; BRx_InstrumentEntityChild = DEMOUS;  *", mappings.getLast().getTargetField());
    }

    @Test
    void extractMappings_standardLastCommittedAtNull() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getStandardMappingContent()))
                        .filename("standard.matmap")
                        .lastCommitter("John Doe")
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertEquals("standard.matmap", mappings.getFirst().getFilename());
        assertEquals("John Doe", mappings.getFirst().getLastCommitter());
        assertNull(mappings.getFirst().getLastCommittedAt());
        assertEquals("sourceField", mappings.getFirst().getSourceField());
        assertEquals("BRx_InstrumentOrigin = BIMS; BRx_InstrumentEntity = DEMOUS;  *", mappings.getFirst().getTargetField());
    }

    @Test
    void extractMappings_complex() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getComplexMappingContent()))
                        .filename("complex.matmap")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertEquals("complex.matmap", mappings.getFirst().getFilename());
        assertEquals("John Doe", mappings.getFirst().getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.getFirst().getLastCommittedAt());
        assertEquals("sourceField", mappings.getFirst().getSourceField());
        assertEquals("BRx_InstrumentIdentifierValue_SEDOL = SEDOL; BRx_InstrumentIdentifierType = SEDOL; BRx_InstrumentIdentifierValue = SEDOL;  *", mappings.getFirst().getTargetField());
    }

    @Test
    void extractMappings_complexNested() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getComplexNestedMappingContent()))
                        .filename("complex_nested.matmap")
                        .lastCommitter("John Doe")
                        .lastCommittedAt(LAST_COMMITTED_AT)
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertEquals("complex_nested.matmap", mappings.getFirst().getFilename());
        assertEquals("John Doe", mappings.getFirst().getLastCommitter());
        assertEquals(LAST_COMMITTED_AT.toInstant(), mappings.getFirst().getLastCommittedAt());
        assertEquals("sourceField", mappings.getFirst().getSourceField());
        assertEquals("BRx_InstrumentIdentifierValue_SEDOL = SEDOL; BRx_InstrumentIdentifierType = SEDOL; BRx_InstrumentIdentifierValue = SEDOL;  *", mappings.getFirst().getTargetField());
        assertEquals("complex_nested.matmap", mappings.getLast().getFilename());
        assertEquals("childSourceField", mappings.getLast().getSourceField());
        assertEquals("BRx_InstrumentIdentifierValueChild_SEDOL = SEDOL; BRx_InstrumentIdentifierTypeChild = SEDOL; BRx_InstrumentIdentifierValueChild = SEDOL;  *", mappings.getLast().getTargetField());
    }

    @Test
    void extractMappings_NullMessages() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getNullMessagesContent()))
                        .filename("null_messages.matmap")
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertTrue(mappings.isEmpty());
    }

    @Test
    void extractMappings_NullMessageList() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getNullMessageListContent()))
                        .filename("null_message_list.matmap")
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertTrue(mappings.isEmpty());
    }

    @Test
    void extractMappings_NullMessageElement() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getNullMessageElementContent()))
                        .filename("null_message_element.matmap")
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertTrue(mappings.isEmpty());
    }

    @Test
    void extractMappings_NullMessageElementValue() {
        List<SpecFile> specFiles = List.of(
                SpecFile.builder()
                        .content(new Content(getNullMessageElementValueContent()))
                        .filename("null_message_element_value.matmap")
                        .build()
        );

        List<Mapping> mappings = directBRxMappingExtractorStrategy.extractMappings(specFiles);

        assertTrue(mappings.isEmpty());
    }

    private Translator getNoBrxMatmapContent() {
        MessageElement messageElement = getMessageElement("someSourceField", "someTargetField");
        MessageDefinition messageDefinition = getMessageDefinition(messageElement, null, null);
        Message message = getMessage(messageDefinition);
        Messages messages = getMessages(List.of(message));
        return getTranslator(messages);
    }

    private Translator getSimpleMatmapContent() {
        MessageElement messageElement = getMessageElement("sourceField", "$\"BRx_someTargetField\"");
        MessageDefinition messageDefinition = getMessageDefinition(messageElement, null, null);
        Message message = getMessage(messageDefinition);
        Messages messages = getMessages(List.of(message));
        return getTranslator(messages);
    }

    private Translator getIDXMatmapContent() {
        MessageElement messageElement = getMessageElement("sourceField", "$\"BRx_someTargetField_IDX\"");
        MessageDefinition messageDefinition = getMessageDefinition(messageElement, null, null);
        Message message = getMessage(messageDefinition);
        Messages messages = getMessages(List.of(message));
        return getTranslator(messages);
    }

    private Translator getSimpleNestedMatmapContent() {
        MessageElement messageElement = getMessageElement("sourceField", "$\"BRx_someTargetField\"");
        MessageElement childMessageElement = getMessageElement("childSourceField", "$\"BRx_someChildTargetField\"");
        messageElement.setChildElements(List.of(childMessageElement));
        MessageDefinition messageDefinition = getMessageDefinition(messageElement, null, null);
        Message message = getMessage(messageDefinition);
        Messages messages = getMessages(List.of(message));
        return getTranslator(messages);
    }

    private Translator getSimpleEmptyChildElementsMatmapContent() {
        MessageElement messageElement = getMessageElement("sourceField", "$\"BRx_someTargetField\"");
        messageElement.setChildElements(List.of());
        MessageDefinition messageDefinition = getMessageDefinition(messageElement, null, null);
        Message message = getMessage(messageDefinition);
        Messages messages = getMessages(List.of(message));
        return getTranslator(messages);
    }

    private Translator getStandardMappingContent() {
        String sampleValue =
                """
                            $V(BRx_InstrumentCurrency_Count,,CLEAR)
                            "$V(BRx_InstrumentClassification_Count,,CLEAR)
                            "$V(BRx_InstrumentOrigin,BIMS)
                            "$V(BRx_InstrumentEntity,DEMOUS)
                        """;

        MessageElement messageElement = getMessageElement("sourceField", sampleValue);
        MessageDefinition messageDefinition = getMessageDefinition(messageElement, null, null);
        Message message = getMessage(messageDefinition);
        Messages messages = getMessages(List.of(message));
        return getTranslator(messages);
    }

    private Translator getStandardNestedMappingContent() {
        String sampleValue =
                """
                            $V(BRx_InstrumentCurrency_Count,,CLEAR)
                            "$V(BRx_InstrumentClassification_Count,,CLEAR)
                            "$V(BRx_InstrumentOrigin,BIMS)
                            "$V(BRx_InstrumentEntity,DEMOUS)
                        """;
        String childSampleValue =
                """
                            $V(BRx_InstrumentCurrencyChild_Count,,CLEAR)
                            "$V(BRx_InstrumentClassificationChild_Count,,CLEAR)
                            "$V(BRx_InstrumentOriginChild,BIMS)
                            "$V(BRx_InstrumentEntityChild,DEMOUS)
                        """;

        MessageElement messageElement = getMessageElement("sourceField", sampleValue);
        MessageElement childMessageElement = getMessageElement("childSourceField", childSampleValue);
        messageElement.setChildElements(List.of(childMessageElement));
        MessageDefinition messageDefinition = getMessageDefinition(messageElement, null, null);
        Message message = getMessage(messageDefinition);
        Messages messages = getMessages(List.of(message));
        return getTranslator(messages);
    }

    private Translator getComplexMappingContent() {
        String sampleValue =
                """
                            $"SEDOL"
                            $V(BRx_InstrumentIdentifier_Count,,INC)
                            $V(BRx_InstrumentIdentifierType_$"BRx_InstrumentIdentifier_Count",SEDOL)
                            $V(BRx_InstrumentIdentifierValue_$"BRx_InstrumentIdentifier_Count",$"SEDOL")
                            $V(BRx_InstrumentIdentifierValue_SEDOL,$"SEDOL")
                        """;
        MessageElement messageElement = getMessageElement("sourceField", sampleValue);
        MessageDefinition messageDefinition = getMessageDefinition(messageElement, null, null);
        Message message = getMessage(messageDefinition);
        Messages messages = getMessages(List.of(message));
        return getTranslator(messages);
    }

    private Translator getComplexNestedMappingContent() {
        String sampleValue =
                """
                            $"SEDOL"
                            $V(BRx_InstrumentIdentifier_Count,,INC)
                            $V(BRx_InstrumentIdentifierType_$"BRx_InstrumentIdentifier_Count",SEDOL)
                            $V(BRx_InstrumentIdentifierValue_$"BRx_InstrumentIdentifier_Count",$"SEDOL")
                            $V(BRx_InstrumentIdentifierValue_SEDOL,$"SEDOL")
                        """;
        String childSampleValue =
                """
                            $"SEDOL"
                            $V(BRx_InstrumentIdentifierChild_Count,,INC)
                            $V(BRx_InstrumentIdentifierTypeChild_$"BRx_InstrumentIdentifierChild_Count",SEDOL)
                            $V(BRx_InstrumentIdentifierValueChild_$"BRx_InstrumentIdentifierChild_Count",$"SEDOL")
                            $V(BRx_InstrumentIdentifierValueChild_SEDOL,$"SEDOL")
                        """;
        MessageElement messageElement = getMessageElement("sourceField", sampleValue);
        MessageElement childMessageElement = getMessageElement("childSourceField", childSampleValue);
        messageElement.setChildElements(List.of(childMessageElement));
        MessageDefinition messageDefinition = getMessageDefinition(messageElement, null, null);
        Message message = getMessage(messageDefinition);
        Messages messages = getMessages(List.of(message));
        return getTranslator(messages);
    }

    private Translator getNullMessagesContent() {
        return getTranslator(null);
    }

    private Translator getNullMessageListContent() {
        Messages messages = getMessages(null);
        return getTranslator(messages);
    }

    private Translator getNullMessageElementContent() {
        MessageDefinition messageDefinition = getMessageDefinition(null, null, null);
        Message message = getMessage(messageDefinition);
        Messages messages = getMessages(List.of(message));
        return getTranslator(messages);
    }

    private Translator getNullMessageElementValueContent() {
        MessageElement messageElement = getMessageElement("nullValueSourceField", null);
        MessageDefinition messageDefinition = getMessageDefinition(messageElement, null, null);
        Message message = getMessage(messageDefinition);
        Messages messages = getMessages(List.of(message));
        return getTranslator(messages);
    }
}