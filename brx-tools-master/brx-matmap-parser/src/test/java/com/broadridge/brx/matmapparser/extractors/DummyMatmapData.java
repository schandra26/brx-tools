package com.broadridge.brx.matmapparser.extractors;

import com.broadridge.brx.matmapparser.model.matmap.*;

import java.util.List;

class DummyMatmapData {

    static MessageElement getMessageElement(String name, String value) {
        MessageElement messageElement = new MessageElement();
        messageElement.setName(name);
        messageElement.setValue(value);
        return messageElement;
    }

    static MessageElement getMessageElementWithParent(String name, String value) {
        MessageElement messageElement = new MessageElement();
        messageElement.setName(name);
        messageElement.setValue(value);
        messageElement.setParent(getMessageElement("parentName", "parentValue"));
        return messageElement;
    }

    static MessageCategory getMessageCategory(String name) {
        MessageCategory messageCategory = new MessageCategory();
        messageCategory.setName(name);
        return messageCategory;
    }

    static MessageDetail getMessageDetail(String name) {
        MessageDetail messageDetail = new MessageDetail();
        messageDetail.setName(name);
        return messageDetail;
    }

    static MessageDefinition getMessageDefinition(MessageElement messageElement, MessageCategory messageCategory, MessageDetail messageDetail) {
        MessageDefinition messageDefinition = new MessageDefinition();
        messageDefinition.setRootMessageElement(messageElement);
        messageDefinition.setMessageCategory(messageCategory);
        messageDefinition.setMessageDetail(messageDetail);
        return messageDefinition;
    }

    static Message getMessage(MessageDefinition messageDefinition) {
        Message message = new Message();
        message.setMessageDefinition(messageDefinition);
        return message;
    }

    static Messages getMessages(List<Message> messagesList) {
        Messages messages = new Messages();
        messages.setMessageList(messagesList);
        return messages;
    }

    static Translator getTranslator(Messages messages) {
        Translator translator = new Translator();
        translator.setMessages(messages);
        translator.setLists(new Lists());
        return translator;
    }

}
