package com.broadridge.brx.matmapparser.model.matmap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class MessageElement {

    @XmlElement(name = "name", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
    private String name;

    @XmlElement(name = "javaObjectType", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
    private String javaObjectType;

    @XmlElement(name = "value", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
    private String value;

    @XmlElement(name = "afterValue", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
    private String afterValue;

    @XmlElement(name = "repeats", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
    private Boolean repeats;

    @XmlElement(name = "messageElement", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
    private List<MessageElement> childElements;

    // Compatibility fields
    private MessageElement parent;
    private MessageCategory messageCategory;
    private MessageDetail messageDetail;
    private String matmapFileName;

}