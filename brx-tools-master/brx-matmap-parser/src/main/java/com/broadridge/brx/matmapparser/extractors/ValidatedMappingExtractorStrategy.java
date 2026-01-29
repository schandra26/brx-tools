package com.broadridge.brx.matmapparser.extractors;

import com.broadridge.brx.matmapparser.logicalmodel.validator.MappingValidator;
import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.model.SpecFile;
import com.broadridge.brx.matmapparser.model.matmap.*;
import com.broadridge.brx.matmapparser.logicalmodel.entities.BRxEntities;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service("validated")
@RequiredArgsConstructor
public class ValidatedMappingExtractorStrategy implements MappingExtractorStrategy {

    private static final String BRX_KEYWORD = "BRx";

    private final MappingValidator mappingValidator;

    @Override
    public List<Mapping> extractMappings(final List<SpecFile> specFiles) {
        List<Mapping> mappings = new ArrayList<>();

        for (SpecFile specFile : specFiles) {
            List<Mapping> extractedMappings = extractOutputMappings(specFile.getContent().getMatmap(), specFile)
                    .stream()
                    .filter(mappingValidator::isValid)
                    .toList();
            mappings.addAll(extractedMappings);
        }

        return mappings;
    }

    private List<Mapping> extractOutputMappings(final Translator translator, final SpecFile specFile) {
        final List<Mapping> outputMappings = new ArrayList<>();

        final List<Message> brxMessages = identifyBRxMessages(translator.getMessages());

        final List<MessageElement> leafBRxMessageElements = extractAttributeMessageElements(brxMessages, specFile.getFilename());

        final List<Message> nonBRxMessages = translator.getMessages().getMessageList().stream()
                .filter(message -> !brxMessages.contains(message))
                .toList();

        final List<MessageElement> leafNonBRxMessageElements = extractAttributeMessageElements(nonBRxMessages, specFile.getFilename());

        for (final MessageElement leafBRxMessageElement : leafBRxMessageElements) {
            final MessageElement leafNonBRxMessageElement = identifyMappedNonBRxMessageElement(leafNonBRxMessageElements, leafBRxMessageElement);
            if (leafNonBRxMessageElement != null) {
                outputMappings.add(generateOutputMapping(leafBRxMessageElement, leafNonBRxMessageElement, specFile));
            }
        }

        return outputMappings;
    }

    private List<Message> identifyBRxMessages(final Messages messages) {
        final List<Message> brxMessages = new ArrayList<>();
        for (final Message message : messages.getMessageList()) {
            if (message.getMessageDefinition().getMessageCategory().getName().equals(BRX_KEYWORD)) {
                brxMessages.add(message);
            }
        }
        if (brxMessages.isEmpty()) {
            for (final Message message : messages.getMessageList()) {
                if (message.getMessageDefinition().getMessageDetail().getName().contains(BRX_KEYWORD)) {
                    brxMessages.add(message);
                }
            }
        }
        return brxMessages;
    }

    private List<MessageElement> extractAttributeMessageElements(final List<Message> messages, final String matmapFileName) {
        return messages.stream()
                .map(Message::getMessageDefinition)
                .map(messageDefinition -> getLeafMessageElements(messageDefinition, matmapFileName))
                .flatMap(List::stream)
                .toList();
    }

    private List<MessageElement> getLeafMessageElements(final MessageDefinition messageDefinition, final String matmapFileName) {
        final List<MessageElement> leafMessageElements = new ArrayList<>();
        final MessageElement rootMessageElement = messageDefinition.getRootMessageElement();
        rootMessageElement.setMessageCategory(messageDefinition.getMessageCategory());
        rootMessageElement.setMessageDetail(messageDefinition.getMessageDetail());
        rootMessageElement.setMatmapFileName(matmapFileName);
        getLeafMessageElements(rootMessageElement, leafMessageElements);
        return leafMessageElements;
    }

    private void getLeafMessageElements(final MessageElement messageElement, final List<MessageElement> leafMessageElements) {
        if (messageElement.getChildElements() == null || messageElement.getChildElements().isEmpty()) {
            messageElement.setMessageCategory(messageElement.getParent().getMessageCategory());
            messageElement.setMessageDetail(messageElement.getParent().getMessageDetail());
            messageElement.setMatmapFileName(messageElement.getParent().getMatmapFileName());
            leafMessageElements.add(messageElement);
        } else {
            for (final MessageElement m : messageElement.getChildElements()) {
                if (messageElement.getJavaObjectType() != null && !messageElement.getJavaObjectType().equals("array")) {
                    m.setParent(messageElement);
                    m.setMessageCategory(m.getParent().getMessageCategory());
                    m.setMessageDetail(m.getParent().getMessageDetail());
                    m.setMatmapFileName(m.getParent().getMatmapFileName());
                    getLeafMessageElements(m, leafMessageElements);
                }
            }
        }
    }

    private MessageElement identifyMappedNonBRxMessageElement(final List<MessageElement> leafNonBRxMessageElements, final MessageElement leafBRxMessageElement) {
        for (final MessageElement leafNonBRxMessageElement : leafNonBRxMessageElements) {
            if ((leafNonBRxMessageElement.getValue() != null && leafNonBRxMessageElement.getJavaObjectType() != null) &&
                    (leafNonBRxMessageElement.getValue().equals(leafBRxMessageElement.getValue()) &&
                    leafNonBRxMessageElement.getJavaObjectType().equals(leafBRxMessageElement.getJavaObjectType()))) {
                    return leafNonBRxMessageElement;
                }

        }
        return null;
    }

    private String getEntityOfTheMapping(final MessageElement messageElement) {
        for (final BRxEntities bRxEntity : BRxEntities.values()) {
            if (StringUtils.containsIgnoreCase(messageElement.getMessageDetail().getName(), bRxEntity.getEntityName())) {
                return bRxEntity.getEntityName();
            }
            final MessageElement parentMessageElement = getParentMessageElement(messageElement);
            if ((parentMessageElement.getName() != null && parentMessageElement.getValue() != null) &&
                    (parentMessageElement.getName().equals(bRxEntity.getEntityName()) || parentMessageElement.getValue().equals(bRxEntity.getEntityName()))) {
                    return bRxEntity.getEntityName();
                }

        }
        return null;
    }

    private MessageElement getParentMessageElement(final MessageElement messageElement) {
        if (messageElement.getParent() == null) {
            return messageElement;
        } else {
            return getParentMessageElement(messageElement.getParent());
        }
    }

    private Mapping generateOutputMapping(final MessageElement leafBRxMessageElement, final MessageElement leafNonBRxMessageElement, final SpecFile specFile) {
        return Mapping.builder()
                .filename(specFile.getFilename())
                .lastCommitter(specFile.getLastCommitter())
                .lastCommittedAt(specFile.getLastCommittedAt() != null ? specFile.getLastCommittedAt().toInstant() : null)
                .targetField(leafBRxMessageElement.getName())
                .sourceField(leafNonBRxMessageElement.getName())
                .entity(getEntityOfTheMapping(leafBRxMessageElement))
                .createdAt(Instant.now())
                .build();
    }
}
