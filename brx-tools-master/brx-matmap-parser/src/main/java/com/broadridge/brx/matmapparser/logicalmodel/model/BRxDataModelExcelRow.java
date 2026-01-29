package com.broadridge.brx.matmapparser.logicalmodel.model;

public record BRxDataModelExcelRow(
        String dataModel,
        String dataEntity,
        String dataAttribute,
        String shortenedEntityName,
        String shortenedLabelName,
        int seq,
        String databaseType,
        String xsdDatatype,
        boolean dependent,
        String enumeratedBy,
        String description,
        String required,
        boolean pii,
        boolean extension,
        boolean deprecated) {
}
