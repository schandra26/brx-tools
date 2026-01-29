package com.broadridge.brx.matmapparser.extractors;

import com.broadridge.brx.matmapparser.logicalmodel.validator.MappingValidator;
import com.broadridge.brx.matmapparser.model.BRxValue;
import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.model.SpecFile;
import com.broadridge.brx.matmapparser.model.matmap.Message;
import com.broadridge.brx.matmapparser.model.matmap.MessageDefinition;
import com.broadridge.brx.matmapparser.model.matmap.MessageElement;
import com.broadridge.brx.matmapparser.logicalmodel.extractor.MappingExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("directBRx")
@Slf4j
@RequiredArgsConstructor
public class DirectBRxMappingExtractorStrategy implements MappingExtractorStrategy {

    private static final Pattern SIMPLE_BRX_PATTERN =
            Pattern.compile("\"([Bb][Rr][Xx]_[\\w]+(?:_\\$\\d+)?)\"");
    private static final Pattern STANDARD_BRX_PATTERN =
            Pattern.compile("\\([Bb][Rr][Xx]_([\\w]+(?:_\\$\\d+)?)(?:,([^,\\)]*))?(?:,([^\\)]*))?\\)");
    private static final Pattern COMPLEX_BRX_PATTERN =
            Pattern.compile("\\([Bb][Rr][Xx]_([\\w]+(?:_\\$\\d+)?)_\\$\"[Bb][Rr][Xx]_([\\w]+(?:_\\$\\d+)?)\"(?:,([^\\)]*))?\\)");
    public static final String COUNT_SUFFIX = "_count";

    private final MappingValidator mappingValidator;

    @Override
    public List<Mapping> extractMappings(final List<SpecFile> specFiles) {
        List<Mapping> mappings = new ArrayList<>();

        for (SpecFile specFile : specFiles) {
            if (hasContent(specFile)) {
                log.info("[matmapFile={}] Processing matmap file", specFile.getFilename());
                List<MessageElement> messageElements = specFile.getContent().getMatmap().getMessages().getMessageList().stream()
                        .map(Message::getMessageDefinition)
                        .map(MessageDefinition::getRootMessageElement)
                        .toList();

                messageElements.forEach(
                        messageElement -> processMessageElement(messageElement, specFile, mappings)
                );
            } else {
                log.info("[matmapFile={}] No messages found", specFile.getFilename());
            }
        }

        return mappings;
    }

    private boolean hasContent(SpecFile specFile) {
        return specFile.getContent().getMatmap().getMessages() != null &&
                specFile.getContent().getMatmap().getMessages().getMessageList() != null;
    }

    private void processMessageElement(MessageElement element, SpecFile specFile, List<Mapping> mappings) {
        if (shouldProcessElement(element)) {
            String cleanValue = getCleanValue(element.getValue());

            if (!mappingValidator.containsBRxDataAttribute(cleanValue)) {
                cleanValue = cleanValue + " *";
            }

            Mapping mapping = Mapping.builder()
                    .sourceField(element.getName())
                    .targetField(cleanValue)
                    .filename(specFile.getFilename())
                    .lastCommitter(specFile.getLastCommitter())
                    .lastCommittedAt(specFile.getLastCommittedAt() != null ? specFile.getLastCommittedAt().toInstant() : null)
                    .createdAt(Instant.now())
                    .build();
            mappings.add(mapping);
        }

        // Check for child elements and process them recursively
        if (element != null && element.getChildElements() != null && !element.getChildElements().isEmpty()) {
            element.getChildElements().forEach(
                    childElement -> processMessageElement(childElement, specFile, mappings)
            );
        }
    }

    private boolean shouldProcessElement(MessageElement element) {
        return element != null && element.getValue() != null &&
               element.getValue().toLowerCase().contains("brx_") &&
               !element.getValue().contains("_IDX");
    }

    private String getCleanValue(String dirtyValue) {
        Map<String, BRxValue> entries = new LinkedHashMap<>();

        Map<String, BRxValue> simpleBrxEntries = extractFromSimpleBrxPattern(dirtyValue);
        entries.putAll(simpleBrxEntries);

        Map<String, BRxValue> standardBrxValues = extractFromStandardBrxPattern(dirtyValue);
        entries.putAll(standardBrxValues);

        Map<String, BRxValue> complexBrxValues = extractFromComplexBrxPattern(dirtyValue);
        entries.putAll(complexBrxValues);

        List<BRxValue> brxValues = new ArrayList<>(entries.values());
        return formatExtractedEntries(brxValues);
    }

    private Map<String, BRxValue> extractFromSimpleBrxPattern(String dirtyValue) {
        Map<String, BRxValue> entries = new LinkedHashMap<>();

        Matcher standaloneMatcher = SIMPLE_BRX_PATTERN.matcher(dirtyValue);
        while (standaloneMatcher.find()) {
            String keyName = standaloneMatcher.group(1);
            if (keyName.toLowerCase().contains(COUNT_SUFFIX)) {
                continue;
            }

            entries.computeIfAbsent(keyName, k -> new BRxValue(keyName, "", ""));
        }

        return entries;
    }

    private Map<String, BRxValue> extractFromStandardBrxPattern(String dirtyValue) {
        Map<String, BRxValue> entries = new LinkedHashMap<>();

        Matcher matcher = STANDARD_BRX_PATTERN.matcher(dirtyValue);
        while (matcher.find()) {
            String keyName = "BRx_" + matcher.group(1);

            if (keyName.toLowerCase().contains(COUNT_SUFFIX)) {
                continue;
            }

            String value1 = matcher.groupCount() > 1 && matcher.group(2) != null ?
                    matcher.group(2).trim() : "";
            String value2 = matcher.groupCount() > 2 && matcher.group(3) != null ?
                    matcher.group(3).trim() : "";

            String cleanValue1 = cleanValue(value1);
            String cleanValue2 = cleanValue(value2);

            entries.put(keyName, new BRxValue(keyName, cleanValue1, cleanValue2));
        }

        return entries;
    }

    private Map<String, BRxValue> extractFromComplexBrxPattern(String dirtyValue) {
        Map<String, BRxValue> entries = new LinkedHashMap<>();

        Matcher complexMatcher = COMPLEX_BRX_PATTERN.matcher(dirtyValue);

        while (complexMatcher.find()) {
            String prefix = complexMatcher.group(1);

            if (prefix.toLowerCase().contains(COUNT_SUFFIX)) {
                continue;
            }

            String value = complexMatcher.groupCount() > 2 && complexMatcher.group(3) != null ?
                    complexMatcher.group(3).trim() : "";

            String cleanValue = cleanValue(value);

            String fullKey = "BRx_" + prefix;
            entries.put(fullKey, new BRxValue(fullKey, cleanValue, ""));
        }

        return entries;
    }

    private String formatExtractedEntries(List<BRxValue> brxValues) {
        StringBuilder sb = new StringBuilder();

        for (BRxValue value : brxValues) {
            if (valueContainsOnlyKey(value)) {
                sb.append(value.getKey()).append("; ");
            }

            if (valueContainsOnlyValue1(value)) {
                sb.append(value.getKey()).append(" = ").append(value.getValue1()).append("; ");
            }

            if (valueContainsValue1AndValue2(value)) {
                sb.append(value.getKey()).append(" = ").append(value.getValue1()).append(",").append(value.getValue2()).append("; ");
            }

            if (valueContainsOnlyValue2(value)) {
                sb.append(value.getKey()).append(" = ").append(value.getValue2()).append("; ");
            }
        }

        return sb.toString();
    }

    private boolean valueContainsOnlyKey(BRxValue value) {
        return value.getValue1().isBlank() && value.getValue2().isBlank();
    }

    private boolean valueContainsOnlyValue1(BRxValue value) {
        return !value.getValue1().isBlank() && value.getValue2().isBlank();
    }

    private boolean valueContainsValue1AndValue2(BRxValue value) {
        return !value.getValue1().isBlank() && !value.getValue2().isBlank();
    }

    private boolean valueContainsOnlyValue2(BRxValue value) {
        return !value.getValue2().isBlank() && value.getValue1().isBlank();
    }

    private String cleanValue(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        if ((value.startsWith("$\"") && value.endsWith("\"")) ||
            (value.startsWith("$\"") && value.endsWith("_$1\""))) {
            return value.substring(2, value.length() - 1);
        }

        return value;
    }
}
