package com.broadridge.brx.matmapparser.model.matmap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Translator", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class Translator {

    @XmlElement(name = "Messages", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
    private Messages messages;

    @XmlElement(name = "Lists", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
    private Lists lists;

}
