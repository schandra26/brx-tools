package com.broadridge.brx.matmapparser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class BRxValue {

    private String key;
    private String value1;
    private String value2;

}