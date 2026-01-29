package com.broadridge.brx.matmapparser.logicalmodel.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BRxEnumerationMetadataExcelColumn {

    ENUMERATION_TYPE(0),
    ENUMERATION_VALUE(1),
    ENUMERATION_DESCRIPTION(2);

    private final int index;
}
