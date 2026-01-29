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
public class Messages {

    @XmlElement(name = "message", namespace = "http://messageautomation.com/translator/v1/XMLSchema.xsd")
    private List<Message> messageList;

}
