package com.broadridge.brx.matmapparser.parsers;

import com.broadridge.brx.matmapparser.logicalmodel.enumeration.BRxEnumerationMetadata;
import com.broadridge.brx.matmapparser.logicalmodel.model.BRxDataModel;

import java.util.List;

public interface ExcelParser {

    List<BRxDataModel> getBrxDataModels();

    BRxEnumerationMetadata getBRxEnumerationMetadata();

}
