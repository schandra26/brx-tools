package com.broadridge.brx.matmapparser.logicalmodel.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BRxDataModelExcelColumn {
    DATA_MODEL(0),
    DATA_ENTITY(1),
    DATA_ATTRIBUTE(2),
    SHORTENED_ENTITY_NAME(3),
    SHORTENED_LABEL_NAME(4),
    SEQ(5),
    DATABASE_TYPE(6),
    XSD_DATATYPE(7),
    DEPENDENT(8),
    ENUMERATED_BY(9),
    DESCRIPTION(10),
    IS_REQUIRED(11),
    PII(12),
    IS_EXTENSION(15),
    DEPRECATED(16);

    private final int index;
}
