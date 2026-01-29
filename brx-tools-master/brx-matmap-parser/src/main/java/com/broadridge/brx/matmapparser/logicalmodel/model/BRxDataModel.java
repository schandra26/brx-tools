package com.broadridge.brx.matmapparser.logicalmodel.model;

import java.util.List;

public record BRxDataModel(String name, List<BRxDataModelExcelRow> records) {
}
