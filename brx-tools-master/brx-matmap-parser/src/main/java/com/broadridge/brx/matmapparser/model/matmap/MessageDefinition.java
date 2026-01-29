package com.broadridge.brx.matmapparser.model.matmap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class MessageDefinition {

    @XmlElement(name = "messageCategory", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
    private MessageCategory messageCategory;

    @XmlElement(name = "messageDetail", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
    private MessageDetail messageDetail;

    @XmlElement(name = "messageElement", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
    private MessageElement rootMessageElement;

}